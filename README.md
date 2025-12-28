# capacitor-video-player

Capacitor plugin for video playing, backed up by FFMPEG for audio decoding of unsupported formats.

## Install

```bash
npm install capacitor-video-player
npx cap sync
```

## API

<docgen-index>

* [`initPlayer()`](#initplayer)
* [`isPlaying()`](#isplaying)
* [`setVideoUrl(...)`](#setvideourl)
* [`playerPlay()`](#playerplay)
* [`playerPause()`](#playerpause)
* [`seekForward()`](#seekforward)
* [`seekBackward()`](#seekbackward)
* [`seekStart()`](#seekstart)
* [`seekEnd()`](#seekend)
* [`getCurrentTime()`](#getcurrenttime)
* [`getDuration()`](#getduration)
* [`getBuffered()`](#getbuffered)
* [`playerStop()`](#playerstop)
* [`exitPlayer()`](#exitplayer)
* [`addListener('CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd' | 'CapVideoPlayerSelectedSubtitlesStream' | 'CapVideoPlayerCurrentTime' | 'CapVideoPlayerHLSProgramDateTimeTag' | 'CapVideoPlayerHLSTargetDuration' | 'CapVideoPlayerSubtitleStreams', ...)`](#addlistenercapvideoplayerbuffering--capvideoplayeridle--capvideoplayerplaying--capvideoplayerready--capvideoplayererror--capvideoplayerend--capvideoplayerselectedsubtitlesstream--capvideoplayercurrenttime--capvideoplayerhlsprogramdatetimetag--capvideoplayerhlstargetduration--capvideoplayersubtitlestreams-)
* [`removeAllListeners()`](#removealllisteners)
* [`selectSubtitleStream(...)`](#selectsubtitlestream)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initPlayer()

```typescript
initPlayer() => Promise<capVideoPlayerResult>
```

Initialize a video player

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### isPlaying()

```typescript
isPlaying() => Promise<capVideoPlayerResult>
```

Return if a given playerId is playing

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### setVideoUrl(...)

```typescript
setVideoUrl(options: { url: string; }) => Promise<capVideoPlayerResult>
```

Play the current video from a given playerId

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### playerPlay()

```typescript
playerPlay() => Promise<void>
```

--------------------


### playerPause()

```typescript
playerPause() => Promise<capVideoPlayerResult>
```

Pause the current video from a given playerId

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### seekForward()

```typescript
seekForward() => Promise<void>
```

--------------------


### seekBackward()

```typescript
seekBackward() => Promise<void>
```

--------------------


### seekStart()

```typescript
seekStart() => Promise<void>
```

Seek to the start of the video

--------------------


### seekEnd()

```typescript
seekEnd() => Promise<void>
```

Seek to the end of the video

--------------------


### getCurrentTime()

```typescript
getCurrentTime() => Promise<{ currentTime: number; }>
```

Get the current playback position in milliseconds

**Returns:** <code>Promise&lt;{ currentTime: number; }&gt;</code>

--------------------


### getDuration()

```typescript
getDuration() => Promise<{ duration: number; }>
```

Get the total duration of the video in milliseconds

**Returns:** <code>Promise&lt;{ duration: number; }&gt;</code>

--------------------


### getBuffered()

```typescript
getBuffered() => Promise<TimeRanges | null>
```

Get the buffered position in milliseconds

**Returns:** <code>Promise&lt;<a href="#timeranges">TimeRanges</a> | null&gt;</code>

--------------------


### playerStop()

```typescript
playerStop() => Promise<capVideoPlayerResult>
```

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### exitPlayer()

```typescript
exitPlayer() => Promise<capVideoPlayerResult>
```

Exit player

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### addListener('CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd' | 'CapVideoPlayerSelectedSubtitlesStream' | 'CapVideoPlayerCurrentTime' | 'CapVideoPlayerHLSProgramDateTimeTag' | 'CapVideoPlayerHLSTargetDuration' | 'CapVideoPlayerSubtitleStreams', ...)

```typescript
addListener(eventName: 'CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd' | 'CapVideoPlayerSelectedSubtitlesStream' | 'CapVideoPlayerCurrentTime' | 'CapVideoPlayerHLSProgramDateTimeTag' | 'CapVideoPlayerHLSTargetDuration' | 'CapVideoPlayerSubtitleStreams', listenerFunc: (event: any) => void) => Promise<PluginListenerHandle>
```

Add event listeners for player events.

| Param              | Type                                                                                                                                                                                                                                                                                                                                                          | Description                                       |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------- |
| **`eventName`**    | <code>'CapVideoPlayerBuffering' \| 'CapVideoPlayerIdle' \| 'CapVideoPlayerPlaying' \| 'CapVideoPlayerReady' \| 'CapVideoPlayerError' \| 'CapVideoPlayerEnd' \| 'CapVideoPlayerSelectedSubtitlesStream' \| 'CapVideoPlayerCurrentTime' \| 'CapVideoPlayerHLSProgramDateTimeTag' \| 'CapVideoPlayerHLSTargetDuration' \| 'CapVideoPlayerSubtitleStreams'</code> | The name of the event to listen for.              |
| **`listenerFunc`** | <code>(event: any) =&gt; void</code>                                                                                                                                                                                                                                                                                                                          | The function to call when the event is triggered. |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### selectSubtitleStream(...)

```typescript
selectSubtitleStream(options: { language: string | null; }) => Promise<void>
```

| Param         | Type                                       |
| ------------- | ------------------------------------------ |
| **`options`** | <code>{ language: string \| null; }</code> |

--------------------


### Interfaces


#### capVideoPlayerResult

| Prop          | Type                 | Description                                   |
| ------------- | -------------------- | --------------------------------------------- |
| **`result`**  | <code>boolean</code> | result set to true when successful else false |
| **`method`**  | <code>string</code>  | method name                                   |
| **`value`**   | <code>any</code>     | value returned                                |
| **`message`** | <code>string</code>  | message string                                |


#### TimeRanges

| Prop         | Type                | Description           |
| ------------ | ------------------- | --------------------- |
| **`length`** | <code>number</code> | Number of time ranges |

| Method    | Signature                    | Description                                       |
| --------- | ---------------------------- | ------------------------------------------------- |
| **start** | (index: number) =&gt; number | Start time of the specified range in milliseconds |
| **end**   | (index: number) =&gt; number | End time of the specified range in milliseconds   |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
