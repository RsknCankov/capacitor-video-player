# capacitor-video-player

Capacitor plugin for video playing

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
* [`playerStop()`](#playerstop)
* [`exitPlayer()`](#exitplayer)
* [`addListener('CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd', ...)`](#addlistenercapvideoplayerbuffering--capvideoplayeridle--capvideoplayerplaying--capvideoplayerready--capvideoplayererror--capvideoplayerend-)
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


### addListener('CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd', ...)

```typescript
addListener(eventName: 'CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd', listenerFunc: (event: any) => void) => Promise<PluginListenerHandle>
```

Add event listeners for player events.

| Param              | Type                                                                                                                                                               | Description                                       |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------- |
| **`eventName`**    | <code>'CapVideoPlayerBuffering' \| 'CapVideoPlayerIdle' \| 'CapVideoPlayerPlaying' \| 'CapVideoPlayerReady' \| 'CapVideoPlayerError' \| 'CapVideoPlayerEnd'</code> | The name of the event to listen for.              |
| **`listenerFunc`** | <code>(event: any) =&gt; void</code>                                                                                                                               | The function to call when the event is triggered. |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### Interfaces


#### capVideoPlayerResult

| Prop          | Type                 | Description                                   |
| ------------- | -------------------- | --------------------------------------------- |
| **`result`**  | <code>boolean</code> | result set to true when successful else false |
| **`method`**  | <code>string</code>  | method name                                   |
| **`value`**   | <code>any</code>     | value returned                                |
| **`message`** | <code>string</code>  | message string                                |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
