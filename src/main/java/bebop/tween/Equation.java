/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bebop.tween;

/**
 * jQuery.easing.method( null†, current_time, start_value, end_value, total_time)
 * 
 * @version 2011/12/08 15:28:48
 */
public enum Equation {

    /** The easing type. */
    easeInQuad {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            return c * (t /= d) * t + b;
        }
    },

    /** The easing type. */
    easeOutQuad {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            return -c * (t /= d) * (t - 2) + b;
        }
    },

    /** The easing type. */
    easeInOutQuad {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            if ((t /= d / 2) < 1) return c / 2 * t * t + b;
            return -c / 2 * ((--t) * (t - 2) - 1) + b;
        }
    },

    /** The easing type. */
    easeInCubic {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            return c * (t /= d) * t * t + b;
        }
    },

    /** The easing type. */
    easeOutCubic {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            return c * ((t = t / d - 1) * t * t + 1) + b;
        }
    },

    /** The easing type. */
    easeInOutCubic {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
            return c / 2 * ((t -= 2) * t * t + 2) + b;
        }
    },

    /** The easing type. */
    easeOutBounce {

        /**
         * {@inheritDoc}
         */
        @Override
        public float compute(float t, float b, float c, float d) {
            if ((t /= d) < (1 / 2.75)) {
                return (float) (c * (7.5625 * t * t) + b);
            } else if (t < (2 / 2.75)) {
                return (float) (c * (7.5625 * (t -= (1.5 / 2.75)) * t + .75) + b);
            } else if (t < (2.5 / 2.75)) {
                return (float) (c * (7.5625 * (t -= (2.25 / 2.75)) * t + .9375) + b);
            } else {
                return (float) (c * (7.5625 * (t -= (2.625 / 2.75)) * t + .984375) + b);
            }
        }
    };

    /**
     * <p>
     * Computes the next value of the interpolation.
     * </p>
     * 
     * @param t The current time.
     * @param b The initial value.
     * @param c The end value.
     * @param d The total time.
     * @return
     */
    public abstract float compute(float t, float b, float c, float d);

}
