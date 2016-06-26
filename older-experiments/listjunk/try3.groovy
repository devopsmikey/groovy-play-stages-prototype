
instance = this.class.classLoader.loadClass( "thingfrombase3", true, false )?.newInstance()
instance.print_my_args('test','foo', ['a','b'])
instance.do_something('1', 'b', ['a','b'])
instance.do_something('1', 'b', [])
