import {WebPlugin} from '@capacitor/core';
import type {CapacitorVideoPlayerPlugin, capVideoPlayerResult, TimeRanges} from './definitions';

export class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    seekForward(): Promise<void> {
        return Promise.resolve();
    }

    seekBackward(): Promise<void> {
        return Promise.resolve();
    }

    seekStart(): Promise<void> {
        console.log('seekStart method not implemented.');
        return Promise.resolve();
    }

    seekEnd(): Promise<void> {
        console.log('seekEnd method not implemented.');
        return Promise.resolve();
    }

    selectSubtitleStream(options: { language: string | null }): Promise<void> {
        console.log(`Selected stream: ${options.language}`);
        return Promise.resolve();
    }

    getCurrentSubtitleStream(): Promise<{ subtitles: string }> {
        return Promise.resolve({subtitles: 'subtitles'});
    }

    initPlayer(): Promise<capVideoPlayerResult> {
        console.log('initPlayer method not implemented.');
        return Promise.resolve({result: false, method: 'initPlayer', message: 'Method not implemented.'});
    }

    isPlaying(): Promise<capVideoPlayerResult> {
        console.log('isPlaying method not implemented.');
        return Promise.resolve({result: false, method: 'isPlaying', message: 'Method not implemented.'});
    }

    setVideoUrl(_options: { url: string }): Promise<capVideoPlayerResult> {
        console.log('setVideoUrl method not implemented.');
        return Promise.resolve({result: false, method: 'setVideoUrl', message: 'Method not implemented.'});
    }

    playerPlay(): Promise<void> {
        console.log('playerPlay method not implemented.');
        return Promise.resolve();
    }

    playerPause(): Promise<capVideoPlayerResult> {
        console.log('playerPause method not implemented.');
        return Promise.resolve({result: false, method: 'playerPause', message: 'Method not implemented.'});
    }

    getCurrentTime(): Promise<{ currentTime: number }> {
        console.log('getCurrentTime method not implemented.');
        return Promise.resolve({currentTime: 0});
    }

    getDuration(): Promise<{ duration: number }> {
        console.log('getDuration method not implemented.');
        return Promise.resolve({duration: 0});
    }

    getBuffered(): Promise<TimeRanges | null> {
        console.log('getBuffered method not implemented.');
        return Promise.resolve(null);
    }

    playerStop(): Promise<capVideoPlayerResult> {
        console.log('playerStop method not implemented.');
        return Promise.resolve({result: false, method: 'playerStop', message: 'Method not implemented.'});
    }

    exitPlayer(): Promise<capVideoPlayerResult> {
        console.log('exitPlayer method not implemented.');
        return Promise.resolve({result: false, method: 'exitPlayer', message: 'Method not implemented.'});
    }

    async echo(options: { value: string }): Promise<{ value: string }> {
        console.log('ECHO', options);
        return options;
    }
}