import { CapacitorVideoPlayer } from 'capacitor-video-player';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    CapacitorVideoPlayer.echo({ value: inputValue })
}
