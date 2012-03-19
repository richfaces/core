/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.application.push.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicy;
import org.atmosphere.cpr.Meteor;
import org.atmosphere.websocket.WebSocket;
import org.richfaces.application.push.Request;
import org.richfaces.application.push.Session;
import org.richfaces.log.Logger;
import org.richfaces.log.RichfacesLogger;

/**
 * @author Nick Belaevski
 * @author Lukas Fryc
 */
public class RequestImpl implements Request, AtmosphereResourceEventListener {
    private static final Logger LOGGER = RichfacesLogger.APPLICATION.getLogger();
    private static final int SUSPEND_TIMEOUT = 30 * 1000;
    private Session session;
    private final Meteor meteor;
    private AtomicBoolean hasActiveBroadcaster = new AtomicBoolean(false);
    private BroadcasterLifeCyclePolicy policy;

    public RequestImpl(Meteor meteor, Session session) {
        super();

        this.meteor = meteor;
        meteor.addListener(this);

        this.session = session;

        // Set policy to EMPTY_DESTROY so that Broadcaster is removed from BroadcasterFactory and releases resources if
        // there is no AtmosphereResource associated with it.
        policy = new BroadcasterLifeCyclePolicy.Builder().policy(
                BroadcasterLifeCyclePolicy.ATMOSPHERE_RESOURCE_POLICY.EMPTY_DESTROY).build();
        this.meteor.getBroadcaster().setBroadcasterLifeCyclePolicy(policy);
    }

    public void suspend() {
        meteor.suspend(SUSPEND_TIMEOUT, isPolling());
    }

    public void resume() {
        meteor.resume();
    }

    public boolean isPolling() {
        HttpServletRequest req = meteor.getAtmosphereResource().getRequest();
        boolean isWebsocket = req.getAttribute(WebSocket.WEBSOCKET_SUSPEND) != null
                || req.getAttribute(WebSocket.WEBSOCKET_RESUME) != null;

        // TODO how to detect non-polling transports?
        return !isWebsocket;
    }

    public Session getSession() {
        return session;
    }

    /**
     * <p>
     * Tries to push messages, when there are some in the session's queue.
     * </p>
     *
     * <p>
     * When detects that request is currently broadcasting, it ignores the call, since the sending of the messages will be
     * proceed later as stated by {@link #onBroadcast(AtmosphereResourceEvent)} method.
     * </p>
     */
    public void postMessages() {
        if (!session.getMessages().isEmpty()) {
            if (lockBroadcaster()) {
                if (!session.getMessages().isEmpty()) {
                    meteor.getBroadcaster().broadcast(new MessageDataScriptString(getSession().getMessages()));
                } else {
                    unlockBroadcaster();
                    // since no messages were sent, it might happen that someone called postMessages and there are new messages
                    // waiting, if so, we try to post them
                    postMessages();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.atmosphere.cpr.AtmosphereResourceEventListener#onSuspend(org.atmosphere.cpr.AtmosphereResourceEvent)
     */
    public void onSuspend(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
        try {
            getSession().connect(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            getSession().disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onResume(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
        disconnect();
    }

    public void onDisconnect(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
        disconnect();
    }

    /**
     * <p>
     * This method is called once the broadcast event occurs.
     * </p>
     *
     * <p>
     * Once this event occurs, he broadcasting is done, so we can clean up.
     * </p>
     *
     * <p>
     * This method clears the broadcasted messages from session and then opens the request for further broadcasting.
     * </p>
     *
     * <p>
     * In case this request is long-polling, the request is completed and client needs to start new request for receiving new
     * messages.
     * </p>
     *
     * <p>
     * In another case - the request is done by websocket - it tries to send messages which could be posted when broadcasting.
     * </p>
     */
    public void onBroadcast(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
        MessageDataScriptString serializedMessages = (MessageDataScriptString) event.getMessage();
        getSession().clearBroadcastedMessages(serializedMessages.getLastSequenceNumber());

        unlockBroadcaster();

        if (isPolling()) {
            event.getResource().resume();
        } else {
            postMessages();
        }
    }

    public void onThrowable(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
        // TODO Auto-generated method stub
        Throwable throwable = event.throwable();
        LOGGER.error(throwable.getMessage(), throwable);
    }

    /**
     * <p>
     * Use atomic operation to try locking broadcaster.
     * </p>
     *
     * <p>
     * You must ensure that after each call to this method with result true, you will call {@link #unlockBroadcaster()} method
     * later once broadcaster is ready to broadcaster new messages.
     * </p>
     *
     * @return true if the broadcaster has been locked; false otherwise
     */
    private boolean lockBroadcaster() {
        return hasActiveBroadcaster.compareAndSet(false, true);
    }

    /**
     * Unlocks broadcaster.
     *
     * @throws IllegalStateException when this method is called in state where the broadcaster is not blocked
     */
    private void unlockBroadcaster() {
        boolean previousValue = hasActiveBroadcaster.getAndSet(false);
        if (false == previousValue) {
            throw new IllegalStateException("Request should be blocked in time of broadcasting");
        }
    }
}
