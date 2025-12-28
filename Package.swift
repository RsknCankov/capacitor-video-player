// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorVideoPlayer",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorVideoPlayer",
            targets: ["CapacitorVideoPlayerPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorVideoPlayerPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorVideoPlayerPlugin"),
        .testTarget(
            name: "CapacitorVideoPlayerPluginTests",
            dependencies: ["CapacitorVideoPlayerPlugin"],
            path: "ios/Tests/CapacitorVideoPlayerPluginTests")
    ]
)