instance = this.class.classLoader.loadClass( "TEMPLATE_stage", true, false )?.newInstance()
println instance.get_stage_name()
println instance.get_step_names()
instance.Step_STEP1_NAME('test','foo','bar')
