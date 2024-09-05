'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const CapacitorVideoPlayer = core.registerPlugin('CapacitorVideoPlayer', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.CapacitorVideoPlayerWeb()),
});

class CapacitorVideoPlayerWeb extends core.WebPlugin {
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

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    CapacitorVideoPlayerWeb: CapacitorVideoPlayerWeb
});

exports.CapacitorVideoPlayer = CapacitorVideoPlayer;
//# sourceMappingURL=plugin.cjs.js.map
