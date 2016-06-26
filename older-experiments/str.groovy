def nvar = null
def evar = ""
def var = "var"

def testit(STR) {
println "testit($STR)"
if (!STR) {
   println "true not $STR"
} else { 
    println "false not $STR"
}
println ""
}

testit(nvar)
testit(evar)
testit(var)
