instance = this.class.classLoader.loadClass( "thingfrombase", true, false )?.newInstance()
instance.print_my_args('test','foo','bar')
instance.do_something('1', 'b', 'three')
