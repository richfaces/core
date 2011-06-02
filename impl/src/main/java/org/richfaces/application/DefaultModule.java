package org.richfaces.application;

import org.richfaces.application.configuration.ConfigurationService;
import org.richfaces.application.configuration.ConfigurationServiceImpl;
import org.richfaces.application.push.PushContextFactory;
import org.richfaces.application.push.impl.PushContextFactoryImpl;
import org.richfaces.cache.Cache;
import org.richfaces.el.GenericsIntrospectionService;
import org.richfaces.el.GenericsIntrospectionServiceImpl;
import org.richfaces.javascript.JavaScriptService;
import org.richfaces.javascript.JavaScriptServiceImpl;
import org.richfaces.l10n.BundleLoader;
import org.richfaces.renderkit.AjaxDataSerializer;
import org.richfaces.renderkit.AjaxDataSerializerImpl;
import org.richfaces.resource.DefaultResourceCodec;
import org.richfaces.resource.ResourceCodec;
import org.richfaces.resource.ResourceLibraryFactory;
import org.richfaces.resource.ResourceLibraryFactoryImpl;
import org.richfaces.skin.SkinFactory;
import org.richfaces.skin.SkinFactoryImpl;

public class DefaultModule implements Module {
    public void configure(ServicesFactory factory) {
        factory.setInstance(ConfigurationService.class, new ConfigurationServiceImpl());
        factory.setInstance(SkinFactory.class, new SkinFactoryImpl());
        factory.setInstance(AjaxDataSerializer.class, new AjaxDataSerializerImpl());
        factory.setInstance(ResourceCodec.class, ServiceLoader.loadService(ResourceCodec.class, DefaultResourceCodec.class));
        factory.setInstance(Cache.class, new CacheProvider());
        factory.setInstance(Uptime.class, new Uptime());
        factory.setInstance(DependencyInjector.class, new DependencyInjectionServiceImpl());
        factory.setInstance(MessageFactory.class, new MessageFactoryImpl(new BundleLoader()));
        factory.setInstance(ResourceLibraryFactory.class, new ResourceLibraryFactoryImpl());
        factory.setInstance(PushContextFactory.class,
            ServiceLoader.loadService(PushContextFactory.class, PushContextFactoryImpl.class));
        factory.setInstance(JavaScriptService.class, new JavaScriptServiceImpl());
        factory.setInstance(GenericsIntrospectionService.class, new GenericsIntrospectionServiceImpl());
    }
}
