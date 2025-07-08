tasks.register("buildDockerImage") {
    group = "docker"
    description = "Spring 애플리케이션을 위한 Docker 이미지를 빌드합니다"

    doLast {
        val imageName = "hansabal-spring"

        println("▶ Docker 이미지 빌드를 시작합니다: $imageName")
        exec {
            workingDir = file("..")  // 루트에서 context 잡음 (Dockerfile 위치가 ./infra/Dockerfile이므로)
            commandLine(
                "docker", "build",
                "-f", "infra/Dockerfile",   // 명시적 Dockerfile 경로
                "-t", imageName,
                "."
            )
        }
    }
}