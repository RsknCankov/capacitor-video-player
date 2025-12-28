import { CapacitorVideoPlayer } from 'capacitor-video-player';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    CapacitorVideoPlayer.echo({ value: inputValue })
}

// Example video player functions
window.initVideoPlayer = async () => {
    try {
        await CapacitorVideoPlayer.initPlayer();
        console.log('Video player initialized');
    } catch (error) {
        console.error('Failed to initialize video player:', error);
    }
}

window.setVideoUrl = async () => {
    try {
        const url = document.getElementById("videoUrl").value || "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        await CapacitorVideoPlayer.setVideoUrl({ url });
        console.log('Video URL set');
    } catch (error) {
        console.error('Failed to set video URL:', error);
    }
}

window.playVideo = async () => {
    try {
        await CapacitorVideoPlayer.playerPlay();
        console.log('Video playing');
    } catch (error) {
        console.error('Failed to play video:', error);
    }
}

window.pauseVideo = async () => {
    try {
        await CapacitorVideoPlayer.playerPause();
        console.log('Video paused');
        stopAutoUpdate();
    } catch (error) {
        console.error('Failed to pause video:', error);
    }
}

window.seekVideo = async () => {
    try {
        console.log('seekTo method has been removed. Use seekStart() or seekEnd() instead.');
    } catch (error) {
        console.error('Failed to seek video:', error);
    }
}

window.seekToStart = async () => {
    try {
        await CapacitorVideoPlayer.seekStart();
        console.log('Seeking to start of video');
    } catch (error) {
        console.error('Failed to seek to start:', error);
    }
}

window.seekToEnd = async () => {
    try {
        await CapacitorVideoPlayer.seekEnd();
        console.log('Seeking to end of video');
    } catch (error) {
        console.error('Failed to seek to end:', error);
    }
}

window.getCurrentTime = async () => {
    try {
        const result = await CapacitorVideoPlayer.getCurrentTime();
        console.log('Current time:', result.currentTime, 'ms');
        document.getElementById("currentTimeDisplay").textContent = `${(result.currentTime / 1000).toFixed(1)}s`;
    } catch (error) {
        console.error('Failed to get current time:', error);
    }
}

window.getDuration = async () => {
    try {
        const result = await CapacitorVideoPlayer.getDuration();
        console.log('Duration:', result.duration, 'ms');
        document.getElementById("durationDisplay").textContent = `${(result.duration / 1000).toFixed(1)}s`;
    } catch (error) {
        console.error('Failed to get duration:', error);
    }
}

window.getBuffered = async () => {
    try {
        const result = await CapacitorVideoPlayer.getBuffered();
        console.log('Buffered ranges:', result.timeRanges);
        
        if (result.timeRanges) {
            const timeRanges = result.timeRanges;
            let bufferedInfo = `Ranges: ${timeRanges.length}`;
            
            for (let i = 0; i < timeRanges.length; i++) {
                const start = timeRanges.start[i];
                const end = timeRanges.end[i];
                bufferedInfo += `\nRange ${i}: ${(start / 1000).toFixed(1)}s - ${(end / 1000).toFixed(1)}s`;
            }
            
            document.getElementById("bufferedDisplay").textContent = bufferedInfo;
        } else {
            document.getElementById("bufferedDisplay").textContent = "No buffered ranges";
        }
    } catch (error) {
        console.error('Failed to get buffered ranges:', error);
    }
}

window.stopVideo = async () => {
    try {
        await CapacitorVideoPlayer.playerStop();
        console.log('Video stopped');
        stopAutoUpdate();
    } catch (error) {
        console.error('Failed to stop video:', error);
    }
}

// Add event listeners for video player events
window.addEventListener('load', async () => {
    try {
        await CapacitorVideoPlayer.addListener('CapVideoPlayerReady', (event) => {
            console.log('Video player ready:', event);
            // Get duration when player is ready
            getDuration();
        });

        await CapacitorVideoPlayer.addListener('CapVideoPlayerPlaying', (event) => {
            console.log('Video playing:', event);
            // Start auto-updating current time when playing
            startAutoUpdate();
        });

        await CapacitorVideoPlayer.addListener('CapVideoPlayerCurrentTime', (event) => {
            console.log('Current time:', event.currentTime);
            // Update display with current time from event
            document.getElementById("currentTimeDisplay").textContent = `${(event.currentTime / 1000).toFixed(1)}s`;
        });

        await CapacitorVideoPlayer.addListener('CapVideoPlayerError', (event) => {
            console.error('Video player error:', event);
        });
    } catch (error) {
        console.error('Failed to add event listeners:', error);
    }
});

// Auto-update current time every second when playing
let autoUpdateInterval = null;

window.startAutoUpdate = () => {
    if (autoUpdateInterval) {
        clearInterval(autoUpdateInterval);
    }
    autoUpdateInterval = setInterval(async () => {
        try {
            const result = await CapacitorVideoPlayer.getCurrentTime();
            document.getElementById("currentTimeDisplay").textContent = `${(result.currentTime / 1000).toFixed(1)}s`;
        } catch (error) {
            console.error('Failed to auto-update current time:', error);
        }
    }, 1000);
}

window.stopAutoUpdate = () => {
    if (autoUpdateInterval) {
        clearInterval(autoUpdateInterval);
        autoUpdateInterval = null;
    }
}
