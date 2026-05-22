// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "MacControllerApp",
    platforms: [
        .macOS(.v12)
    ],
    targets: [
        .executableTarget(
            name: "MacControllerApp",
            dependencies: [],
            path: "Sources",
            resources: [.process("Resources")]
        )
    ]
)