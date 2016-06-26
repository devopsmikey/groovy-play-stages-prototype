instance = this.class.classLoader.loadClass( "base", true, false )?.newInstance()
instance.print_my_args('test','foo','bar')
