/*
 * Copyright (C) 2021 bytedance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Create Date : 2021/12/3
 */

package com.benyq.tikbili.player.playback;

public interface PlayerEvent {

    /**
     * Player action event type constants.
     *
     */
    class Action {

        public static final int SET_SURFACE = 1001;

        public static final int PREPARE = 1002;

        public static final int START = 1003;

        public static final int PAUSE = 1004;

        public static final int STOP = 1005;

        public static final int RELEASE = 1006;

        public static final int SEEK_TO = 1007;

        public static final int SET_LOOPING = 1008;

        public static final int SET_SPEED = 1009;
    }

    /**
     * Player state event type constants.
     *
     */
    class State {

        public static final int IDLE = 2001;

        public static final int PREPARING = 2002;

        public static final int PREPARED = 2003;

        public static final int STARTED = 2004;

        public static final int PAUSED = 2005;

        public static final int STOPPED = 2006;

        public static final int RELEASED = 2007;

        public static final int COMPLETED = 2008;

        public static final int ERROR = 2009;
    }

    /**
     * Player info event type constants.
     */
    class Info {

        public static final int DATA_SOURCE_REFRESHED = 3001;

        public static final int VIDEO_SIZE_CHANGED = 3002;

        public static final int VIDEO_SAR_CHANGED = 3003;

        public static final int VIDEO_RENDERING_START = 3004;

        public static final int AUDIO_RENDERING_START = 3005;


        public static final int VIDEO_RENDERING_START_BEFORE_START = 3006;


        public static final int BUFFERING_START = 3007;

        public static final int BUFFERING_END = 3008;

        public static final int BUFFERING_UPDATE = 3009;

        public static final int SEEKING_START = 3010;

        public static final int SEEK_COMPLETE = 3011;

        public static final int PROGRESS_UPDATE = 3012;

        public static final int TRACK_INFO_READY = 3013;

        public static final int TRACK_WILL_CHANGE = 3014;

        public static final int TRACK_CHANGED = 3015;

        public static final int CACHE_UPDATE = 3016;

        public static final int SUBTITLE_STATE_CHANGED = 3017;

        public static final int SUBTITLE_LIST_INFO_READY = 3018;

        public static final int SUBTITLE_FILE_LOAD_FINISH = 3019;

        public static final int SUBTITLE_WILL_CHANGE = 3020;

        public static final int SUBTITLE_TEXT_UPDATE = 3021;

        public static final int SUBTITLE_CHANGED = 3022;

        public static final int FRAME_INFO_UPDATE = 3023;
    }
}
