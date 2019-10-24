rootProject.name = "elivi"

include("common")
include("gradle-plugin")
include("cli")

rootProject.children.forEach {
    it.name = "${rootProject.name}-${it.name}"
}
