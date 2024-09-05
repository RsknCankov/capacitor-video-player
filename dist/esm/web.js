import { WebPlugin } from '@capacitor/core';
export class CapacitorVideoPlayerWeb extends WebPlugin {
    initPlayer() {
        throw new Error('Method not implemented.');
    }
    isPlaying() {
        throw new Error('Method not implemented.');
    }
    setVideoUrl(_options) {
        throw new Error('Method not implemented.');
    }
    playerPlay() {
        throw new Error('Method not implemented.');
    }
    playerPause() {
        throw new Error('Method not implemented.');
    }
    playerStop() {
        throw new Error('Method not implemented.');
    }
    exitPlayer() {
        throw new Error('Method not implemented.');
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}
//# sourceMappingURL=web.js.map