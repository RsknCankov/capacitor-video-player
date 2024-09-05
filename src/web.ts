import { WebPlugin } from '@capacitor/core';

import type {
  CapacitorVideoPlayerPlugin,
  capVideoPlayerResult,
} from './definitions';

export class CapacitorVideoPlayerWeb
  extends WebPlugin
  implements CapacitorVideoPlayerPlugin
{
  initPlayer(): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  isPlaying(): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  setVideoUrl(_options: { url: string }): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  playerPlay(): Promise<void> {
    throw new Error('Method not implemented.');
  }

  playerPause(): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  playerStop(): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  exitPlayer(): Promise<capVideoPlayerResult> {
    throw new Error('Method not implemented.');
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

}
