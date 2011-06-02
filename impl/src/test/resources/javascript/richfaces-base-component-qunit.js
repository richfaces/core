RichFaces.QUnit.run(function() {
    module("richfaces-base-component");

    var createMyComponentClass = function (richfaces) {
        MyComponent = function(componentId) {
            $super.constructor.call(this, componentId);
        };

        var $p = {a:function() {
            return "hello"
        }};
        $p = richfaces.BaseComponent.extend(MyComponent, $p);
        var $super = MyComponent.$super;

        jQuery.extend(MyComponent.prototype, (function () {
            return { name:"MyComponent"}
        })());
        equals(MyComponent.$super, richfaces.BaseComponent.prototype, "New component: MyComponent from");
    };

    // BaseComponent.constructor
    test("RichFaces.BaseComponent.constructor", function () {
        expect(5);
        var c = new RichFaces.BaseComponent("myId");
        ok(c instanceof RichFaces.BaseComponent, "inctance of RichFaces.BaseComponent");
        equals(c.name, "BaseComponent", "name");
        equals(c.id, "myId", "id");
        equals(c.toString(), "BaseComponent", "toString");
        equals(c.getEventElement(), "myId", "getEventElement");
    });

    // BaseComponent inheritance
    test("RichFaces.BaseComponent inheritance", function () {
        expect(26);

        createMyComponentClass(RichFaces);

        var c = new MyComponent("myId");
        ok(c instanceof MyComponent, "inctance of MyComponent");
        equals(c.name, "MyComponent", "name");
        equals(c.id, "myId", "id");
        equals(c.toString(), "BaseComponent, MyComponent", "toString");
        equals(c.getEventElement(), "myId", "getEventElement");

        (function (richfaces) {
            MyComponent2 = function(componentId) {
                $super.constructor.call(this, componentId);
            };
            var $p = {b:"b"};
            $p = MyComponent.extend(MyComponent2, $p);
            equals(typeof $p.a, "function", "ComponentCreation: inherit protected method from MyComponent2");
            var $super = MyComponent2.$super;
            jQuery.extend(MyComponent2.prototype, (function () {
                return { name:"MyComponent2" }
            })());
        })(RichFaces);
        equals(MyComponent2.$super, MyComponent.prototype, "New component: MyComponent2 from");

        var c = new MyComponent2("myId");
        ok(c instanceof MyComponent2, "inctance of MyComponent2");
        ok(c instanceof MyComponent, "inctance of MyComponent");
        ok(c instanceof RichFaces.BaseComponent, "inctance of RichFaces.BaseComponent");
        equals(c.name, "MyComponent2", "name");
        equals(c.id, "myId", "id");
        equals(c.toString(), "BaseComponent, MyComponent, MyComponent2", "toString");
        equals(c.getEventElement(), "myId", "getEventElement");

        (function (richfaces) {
            MyComponent3 = function(componentId) {
                $super.constructor.call(this, componentId);
            };
            var $p = {c:"c"};
            $p = MyComponent2.extend(MyComponent3, $p);
            equals(typeof $p.a, "function", "ComponentCreation: inherit protected method from MyComponent2");
            equals(typeof $p.b, "string", "ComponentCreation: inherit static protected property from MyComponent3");
            var $super = MyComponent3.$super;
            jQuery.extend(MyComponent3.prototype, (function () {
                return { name:"MyComponent3" }
            })());
            equals(MyComponent3.$super, MyComponent2.prototype, "New component: MyComponent3 from");


            var c = new MyComponent3("myId");
            ok(c instanceof MyComponent3, "inctance of MyComponent3");
            ok(c instanceof MyComponent2, "inctance of MyComponent2");
            ok(c instanceof MyComponent, "inctance of MyComponent");
            ok(c instanceof RichFaces.BaseComponent, "inctance of RichFaces.BaseComponent");
            equals(c.name, "MyComponent3", "name");
            equals(c.id, "myId", "id");
            equals(c.toString(), "BaseComponent, MyComponent, MyComponent2, MyComponent3", "toString");
            equals(c.getEventElement(), "myId", "getEventElement");

        })(RichFaces);
    });


    test("RichFaces.BaseComponent.attachToDom", function () {
        expect(2);

        var body = document.getElementsByTagName("body")[0];
        RichFaces.QUnit.appendDomElements(body, '<div id="myId">baseComponentMarkup</div><div id="myId2">baseComponentMarkup2</div>');

        var c = new MyComponent("myId");
        c.attachToDom();
        equals(RichFaces.$("myId"), c, "attachToDom without params");
        c.attachToDom("myId2");
        equals(RichFaces.$("myId2"), c, "attachToDom with custom id");
    });

});