/*
 * MIT License
 *
 * Copyright (c) 2017 Mikhalev Ruslan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package one.chest.music.playlist.controller

import groovy.transform.CompileStatic
import one.chest.music.playlist.service.PlaylistService
import org.junit.Test
import ratpack.jackson.JsonRender

import static ratpack.groovy.test.handling.GroovyRequestFixture.handle

@CompileStatic
class TracksHandlerTest {

    @Test
    void addTrack() {
        List<Track> result = []
        def response = handle(new TracksHandler(
                playlist: [addTrack: { Track track -> result << track }] as PlaylistService
        )) {
            method "POST"
            body "trackId=1&albumId=2&duration=5000", "application/x-www-form-urlencoded"
        }
        assert response.bodyText?.empty && response.status.code == 201
        assert result.size() == 1
        assert result[0].trackId == 1
        assert result[0].albumId == 2
        assert result[0].duration == 5000
    }

    @Test
    void addTrackValidate() {
        def response = handle(new TracksHandler(
                playlist: [:] as PlaylistService
        )) {
            method "POST"
            body "albumId=2&duration=3000", "application/x-www-form-urlencoded"
        }
        assert response.bodyText == 'may not be null' && response.status.code == 422
    }

    @Test
    void getTracks() {
        def response = handle(new TracksHandler(
                playlist: [getTracks: {
                    [new Track(albumId: 1, trackId: 2, duration: 5000L), new Track(albumId: 3, trackId: 4, duration: 5000L)]
                }] as PlaylistService
        )) {
            method "GET"
        }
        assert response.rendered(JsonRender).object instanceof List
        List<Track> list = (List<Track>) response.rendered(JsonRender).object
        assert list[0].albumId == 1
        assert list[0].trackId == 2
        assert list[0].duration == 5000
    }

}