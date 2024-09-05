var capacitorExample = (function (exports, core) {
    'use strict';

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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
