plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.3-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
}