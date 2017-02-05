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
package toybox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

import kiss.I;

/**
 * @version 2011/11/20 12:47:05
 */
public class MP3 extends InputStream {

    private boolean HeadRead = false, EndOfStream = false;

    private InputStream Input;

    private long InputOffset = 0;

    private int AvailableAudioBytes = 0;

    public static void main(String[] args) throws Exception {
        Path input = I.locate("e:\\AAA.flv");
        String flv = input.toString();
        Path output = I.locate("e:\\AAA.mp3");

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add("ffmpeg.exe");
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(flv);
        cmd.add("-acodec");

        cmd.add("copy");
        cmd.add(flv.replace(".flv", ".mp3"));
        System.out.println(cmd.toString());
        ProcessBuilder converter = new ProcessBuilder();
        converter.command(cmd);
        try {
            converter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * A constructor specifying the FLV data input stream to use.
     * 
     * @param Input
     */
    public MP3(InputStream Input) {
        this.Input = Input;
    }

    /**
     * Returns the total data offset in the source input stream
     * 
     * @return the data offset
     */
    public long getInputOffset() {
        return InputOffset;
    }

    public int getLength() {
        return AvailableAudioBytes;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        /* close stream */
        this.InputOffset = 0;
        this.AvailableAudioBytes = 0;
        this.HeadRead = false;
        this.EndOfStream = false;
        this.Input.close();
    }

    /**
     * {@inheritDoc}
     */
    public long skip(long n) throws IOException {
        /*
         * skip audio-data bytes, we check in case less than n bits are skipped and continue until
         * all are
         */
        if (this.EndOfStream) return 0;
        if (!this.HeadRead) this.inputReadHead();
        long skipped = 0;
        while (skipped < n) {
            if (this.EndOfStream) break;
            if (this.AvailableAudioBytes <= 0) this.AvailableAudioBytes = this.readTag();
            long need = n - skipped;
            long s = this.Input.skip(need > this.AvailableAudioBytes ? this.AvailableAudioBytes : need);
            skipped += s;
            this.AvailableAudioBytes -= s;
        }
        return skipped;
    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException {
        /* read 1 audio data byte */
        byte[] b = new byte[1];
        this.audioRead(b, 0, 1);
        return b[0];
    }

    /**
     * {@inheritDoc}
     */
    public int read(byte[] b) throws IOException {
        /* read b audio data bytes */
        return this.read(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    public int read(byte[] b, int off, int len) throws IOException {
        /* read len audio data bytes into offset off */
        return this.audioRead(b, off, len);
    }

    /**
     * Reads the FLV file header.
     * 
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private void inputReadHead() throws IOException {
        /* read the FLV file head */
        long firstFrame = this.inputReadUInt32();
        if (firstFrame != 0x464C5601) throw new IOException("Not an FLV file.");
        long flags = this.inputReadUInt8();
        long dataOffset = this.inputReadUInt32();
        this.HeadRead = true;
        this.inputSkip(dataOffset - this.InputOffset);
    }

    /**
     * Reads audio data from the FLV data input stream.
     * 
     * @param b the byte array into which read data should written
     * @param off the offset after which data should be written
     * @param len the number of bytes to read
     * @return the number of bytes actually read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private int audioRead(byte[] b, int off, int len) throws IOException {
        /* read audio data bytes from the stream according to AvailableAudioBytes */
        if (this.EndOfStream) return -1;
        if (!this.HeadRead) this.inputReadHead();
        int read = 0;
        while (read < len) {
            if (this.EndOfStream) break;
            if (this.AvailableAudioBytes <= 0) {
                this.AvailableAudioBytes = this.readTag();
                if (this.AvailableAudioBytes <= 0) continue;
            }
            int need = len - read;
            int r = this.inputRead(b, off + read, need > this.AvailableAudioBytes ? this.AvailableAudioBytes : need);
            if (r < 0)
                break;
            else {
                read += r;
                this.AvailableAudioBytes -= r;
            }
        }
        return read;
    }

    /**
     * Reads an audio block from the FLV data input stream.
     * 
     * @return the number of bytes of audio data read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private int readTag() throws IOException {
        /*
         * reads an flv tag, returns the amount of audio data in the current frame so that it can be
         * read if tag is not an audio tag skip out of this tag so that the next tag can be read
         */
        if (this.EndOfStream) return 0;
        long prevTagSize = inputReadUInt32();
        long tagType = inputReadUInt8();
        int dataSize = (int) inputReadUInt24();
        long timeStamp = inputReadUInt24();
        timeStamp |= inputReadUInt8() << 24;
        long streamId = inputReadUInt24();
        if (dataSize == 0) return 0;
        long mediaInfo = inputReadUInt8();
        dataSize -= 1;
        if (tagType == 0x8)
            return dataSize;
        else if (tagType == 0x9) {
            // System.out.println("Video tag: " + mediaInfo);
            this.inputSkip(dataSize);
            return 0;
        } else {
            this.inputSkip(dataSize);
            return 0;
        }
    }

    /**
     * Skips a number of bytes in the source stream.
     * 
     * @param n the number of bytes to read
     * @return the number of bytes actually skipped
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private long inputSkip(long n) throws IOException {
        /* perform a skip on the source stream */
        long skipped = 0;
        while (skipped < n) {
            long s = this.Input.skip(n - skipped);
            skipped += s;
            if (s <= 0) break;
        }
        this.InputOffset += skipped;
        return skipped;
    }

    /**
     * Reads a byte from the source stream.
     * 
     * @return the read byte
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private int inputRead() throws IOException {
        /* perform a read on the source stream */
        int read = this.Input.read();
        if (read < 0) {
            this.EndOfStream = true;
            return -1;
        } else
            this.InputOffset += 1;
        return read;
    }

    /**
     * Reads bytes from the source stream.
     * 
     * @param b the byte array into which read bytes should be written
     * @return the number of bytes actually read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private int inputRead(byte[] b) throws IOException {
        /* perform a read on the source stream */
        return inputRead(b, 0, b.length);
    }

    /**
     * Reads bytes from the source stream.
     * 
     * @param b the byte array into which read bytes should be written
     * @param off the offset after which read bytes should be written
     * @param len the number of bytes to read
     * @return the number of bytes actually read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private int inputRead(byte[] b, int off, int len) throws IOException {
        /*
         * perform a read on the source stream, the number of read bytes may be less than len call
         * read as many times as necessary to read len bytes, if read returns -1 we are at EOS
         */
        int read = 0;
        while (read < len) {
            int need = len - read;
            int r = this.Input.read(b, off + read, need);
            if (r < 0) {
                this.EndOfStream = true;
                if (read == 0)
                    return -1;
                else
                    break;
            } else {
                read += r;
            }
        }
        this.InputOffset += read;
        return read;
    }

    /**
     * Reads a single byte as an unsigned integer.
     * 
     * @return the numeric value of the byte read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private long inputReadUInt8() throws IOException {
        /* read 1 byte as a 32 bit unsigned integer */
        int val = this.inputRead();
        return val < 0 ? 0 : val;
    }

    /**
     * Reads a three bytes as an unsigned integer.
     * 
     * @return the numeric value of the bytes read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private long inputReadUInt24() throws IOException {
        /* read 3 bytes as a 32 bit unsigned integer */
        byte[] buffer = new byte[4];
        int read = this.inputRead(buffer, 1, 3);
        if (read < 3) return 0;
        return toUInt32(buffer);
    }

    /**
     * Reads four bytes as an unsigned integer.
     * 
     * @return the numeric value of the bytes read
     * @throws IOException if an IO error occurred while reading the source input stream
     */
    private long inputReadUInt32() throws IOException {
        /* read 4 bytes as a 32 bit unsigned integer */
        byte[] buffer = new byte[4];
        int read = this.inputRead(buffer, 0, 4);
        if (read < 4) return 0;
        return toUInt32(buffer);
    }

    /**
     * Converts a four byte array into an unsigned integer.
     * 
     * @param value the byte array to convert
     * @return the numeric value of the byte array
     */
    private long toUInt32(byte[] value) {
        /*
         * convert byte array to a 32 bit unsigned integer (use long, java does not have unsigned
         * types)
         */
        long val = (0xFF & value[0]) << 24;
        val |= (0xFF & value[1]) << 16;
        val |= (0xFF & value[2]) << 8;
        val |= (0xFF & value[3]);
        return val;
    }
}
