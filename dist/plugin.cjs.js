'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const CapacitorVideoPlayer = core.registerPlugin('CapacitorVideoPlayer', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.CapacitorVideoPlayerWeb()),
});

class CapacitorVideoPlayerWeb extends core.WebPlugin {
    seekForward() {
        return Promise.resolve();
    }
    seekBackward() {
        return Promise.resolve();
    }
    seekStart() {
        console.log('seekStart method not implemented.');
        return Promise.resolve();
    }
    seekEnd() {
        console.log('seekEnd method not implemented.');
        return Promise.resolve();
    }
    selectSubtitleStream(options) {
        console.log(`Selected stream: ${options.language}`);
        return Promise.resolve();
    }
    getCurrentSubtitleStream() {
        return Promise.resolve({ subtitles: 'subtitles' });
    }
    initPlayer() {
        console.log('initPlayer method not implemented.');
        return Promise.resolve({ result: false, method: 'initPlayer', message: 'Method not implemented.' });
    }
    isPlaying() {
        console.log('isPlaying method not implemented.');
        return Promise.resolve({ result: false, method: 'isPlaying', message: 'Method not implemented.' });
    }
    setVideoUrl(_options) {
        console.log('setVideoUrl method not implemented.');
        return Promise.resolve({ result: false, method: 'setVideoUrl', message: 'Method not implemented.' });
    }
    playerPlay() {
        console.log('playerPlay method not implemented.');
        return Promise.resolve();
    }
    playerPause() {
        console.log('playerPause method not implemented.');
        return Promise.resolve({ result: false, method: 'playerPause', message: 'Method not implemented.' });
    }
    getCurrentTime() {
        console.log('getCurrentTime method not implemented.');
        return Promise.resolve({ currentTime: 0 });
    }
    getDuration() {
        console.log('getDuration method not implemented.');
        return Promise.resolve({ duration: 0 });
    }
    getBuffered() {
        console.log('getBuffered method not implemented.');
        return Promise.resolve(null);
    }
    playerStop() {
        console.log('playerStop method not implemented.');
        return Promise.resolve({ result: false, method: 'playerStop', message: 'Method not implemented.' });
    }
    exitPlayer() {
        console.log('exitPlayer method not implemented.');
        return Promise.resolve({ result: false, method: 'exitPlayer', message: 'Method not implemented.' });
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    CapacitorVideoPlayerWeb: CapacitorVideoPlayerWeb
});

exports.CapacitorVideoPlayer = CapacitorVideoPlayer;
//# sourceMappingURL=plugin.cjs.js.map
