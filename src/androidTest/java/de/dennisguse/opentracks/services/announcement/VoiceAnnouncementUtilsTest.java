package de.dennisguse.opentracks.services.announcement;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;

import de.dennisguse.opentracks.content.data.TestDataUtil;
import de.dennisguse.opentracks.data.ContentProviderUtils;
import de.dennisguse.opentracks.data.TrackPointIterator;
import de.dennisguse.opentracks.data.models.Distance;
import de.dennisguse.opentracks.data.models.HeartRate;
import de.dennisguse.opentracks.data.models.Speed;
import de.dennisguse.opentracks.data.models.Track;
import de.dennisguse.opentracks.settings.PreferencesUtils;
import de.dennisguse.opentracks.stats.SensorStatistics;
import de.dennisguse.opentracks.stats.TrackStatistics;
import de.dennisguse.opentracks.ui.intervals.IntervalStatistics;
import de.dennisguse.opentracks.util.StringUtils;

@RunWith(AndroidJUnit4.class)
public class VoiceAnnouncementUtilsTest {

    private final Context context = ApplicationProvider.getApplicationContext();
    private ContentProviderUtils contentProviderUtils;

    @Before
    public void setUp() {
        contentProviderUtils = new ContentProviderUtils(context);
        PreferencesUtils.setVoiceAnnounceHeartRate(false);
    }

    @Test
    public void getAnnouncement_metric_speed() {
        TrackStatistics stats = new TrackStatistics();
        stats.setTotalDistance(Distance.of(20000));
        stats.setTotalTime(Duration.ofHours(2).plusMinutes(5).plusSeconds(10));
        stats.setMovingTime(Duration.ofHours(1).plusMinutes(5).plusSeconds(10));
        stats.setMaxSpeed(Speed.of(100));
        stats.setTotalAltitudeGain(6000f);

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, true, true, null, null);

        // then
        assertEquals("total distance 20.00 kilometers in 1 hour 5 minutes 10 seconds at 18.4 kilometers per hour", announcement);
    }

    @Test
    public void getAnnouncement_withInterval_metric_speed() {
        // given
        int numberOfPoints = 1000;
        Pair<Track.Id, TrackStatistics> trackWithStats = TestDataUtil.buildTrackWithTrackPoints(contentProviderUtils, numberOfPoints);
        Track.Id trackId = trackWithStats.first;
        TrackStatistics stats = trackWithStats.second;
        IntervalStatistics.Interval lastInterval;
        try (TrackPointIterator trackPointIterator = contentProviderUtils.getTrackPointLocationIterator(trackId, null)) {
            assertEquals(trackPointIterator.getCount(), numberOfPoints);
            IntervalStatistics intervalStatistics = new IntervalStatistics(Distance.of(1000));
            lastInterval = intervalStatistics.getIntervalList().get(intervalStatistics.getIntervalList().size() - 1);
        }

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, true, true, lastInterval, null);

        // then
        assertEquals("total distance 14.21 kilometers in 16 minutes 39 seconds at 51.2 kilometers per hour Lap speed of 0.0 kilometers per hour", announcement);
    }

    @Test
    public void getAnnouncement_metric_pace() {
        TrackStatistics stats = new TrackStatistics();
        stats.setTotalDistance(Distance.of(20000));
        stats.setTotalTime(Duration.ofHours(2).plusMinutes(5).plusSeconds(10));
        stats.setMovingTime(Duration.ofHours(1).plusMinutes(5).plusSeconds(10));
        stats.setMaxSpeed(Speed.of(100));
        stats.setTotalAltitudeGain(6000f);

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, true, false, null, null);

        // then
        assertEquals("total distance 20.00 kilometers in 1 hour 5 minutes 10 seconds at 3 minutes 15 seconds per kilometer", announcement);
    }

    @Test
    public void getAnnouncement_withInterval_metric_pace() {
        // given
        int numberOfPoints = 1000;
        Pair<Track.Id, TrackStatistics> trackWithStats = TestDataUtil.buildTrackWithTrackPoints(contentProviderUtils, numberOfPoints);
        Track.Id trackId = trackWithStats.first;
        TrackStatistics stats = trackWithStats.second;
        IntervalStatistics.Interval lastInterval;
        try (TrackPointIterator trackPointIterator = contentProviderUtils.getTrackPointLocationIterator(trackId, null)) {
            assertEquals(trackPointIterator.getCount(), numberOfPoints);
            IntervalStatistics intervalStatistics = new IntervalStatistics(Distance.of(1000));
            lastInterval = intervalStatistics.getIntervalList().get(intervalStatistics.getIntervalList().size() - 1);
        }

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, true, false, lastInterval, null);

        // then
        assertEquals("total distance 14.21 kilometers in 16 minutes 39 seconds at 1 minute 10 seconds per kilometer Lap time of 0 minutes 0 seconds per kilometer", announcement);
    }

    @Test
    public void getAnnouncement_imperial_speed() {
        TrackStatistics stats = new TrackStatistics();
        stats.setTotalDistance(Distance.of(20000));
        stats.setTotalTime(Duration.ofHours(2).plusMinutes(5).plusSeconds(10));
        stats.setMovingTime(Duration.ofHours(1).plusMinutes(5).plusSeconds(10));
        stats.setMaxSpeed(Speed.of(100));
        stats.setTotalAltitudeGain(6000f);

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, false, true, null, null);

        // then
        assertEquals("total distance 12.43 miles in 1 hour 5 minutes 10 seconds at 11.4 miles per hour", announcement);
    }

    @Test
    public void getAnnouncement_withInterval_imperial_speed() {
        // given
        int numberOfPoints = 1000;
        Pair<Track.Id, TrackStatistics> trackWithStats = TestDataUtil.buildTrackWithTrackPoints(contentProviderUtils, numberOfPoints);
        Track.Id trackId = trackWithStats.first;
        TrackStatistics stats = trackWithStats.second;
        IntervalStatistics.Interval lastInterval;
        try (TrackPointIterator trackPointIterator = contentProviderUtils.getTrackPointLocationIterator(trackId, null)) {
            assertEquals(trackPointIterator.getCount(), numberOfPoints);
            IntervalStatistics intervalStatistics = new IntervalStatistics(Distance.of(1000));
            lastInterval = intervalStatistics.getIntervalList().get(intervalStatistics.getIntervalList().size() - 1);
        }

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, false, true, lastInterval, null);

        // then
        assertEquals("total distance 8.83 miles in 16 minutes 39 seconds at 31.8 miles per hour Lap speed of 0.0 miles per hour", announcement);
    }

    @Test
    public void getAnnouncement_imperial_pace() {
        TrackStatistics stats = new TrackStatistics();
        stats.setTotalDistance(Distance.of(20000));
        stats.setTotalTime(Duration.ofHours(2).plusMinutes(5).plusSeconds(10));
        stats.setMovingTime(Duration.ofHours(1).plusMinutes(5).plusSeconds(10));
        stats.setMaxSpeed(Speed.of(100));
        stats.setTotalAltitudeGain(6000f);

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, false, false, null, null);

        // then
        assertEquals("total distance 12.43 miles in 1 hour 5 minutes 10 seconds at 5 minutes 15 seconds per mile", announcement);
    }

    @Test
    public void getAnnouncement_withInterval_imperial_pace() {
        // given
        int numberOfPoints = 1000;
        Pair<Track.Id, TrackStatistics> trackWithStats = TestDataUtil.buildTrackWithTrackPoints(contentProviderUtils, numberOfPoints);
        Track.Id trackId = trackWithStats.first;
        TrackStatistics stats = trackWithStats.second;
        IntervalStatistics.Interval lastInterval;
        try (TrackPointIterator trackPointIterator = contentProviderUtils.getTrackPointLocationIterator(trackId, null)) {
            assertEquals(trackPointIterator.getCount(), numberOfPoints);
            IntervalStatistics intervalStatistics = new IntervalStatistics(Distance.of(1000));
            lastInterval = intervalStatistics.getIntervalList().get(intervalStatistics.getIntervalList().size() - 1);
        }

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, false, false, lastInterval, null);

        // then
        assertEquals("total distance 8.83 miles in 16 minutes 39 seconds at 1 minute 53 seconds per mile Lap time of 0 minutes 0 seconds per mile", announcement);
    }

    @Test
    public void getAnnouncement_heart_rate_and_sensor_statistics() {
        PreferencesUtils.setVoiceAnnounceHeartRate(true);

        int numberOfPoints = 1000;
        Pair<Track.Id, TrackStatistics> trackWithStats = TestDataUtil.buildTrackWithTrackPoints(contentProviderUtils, numberOfPoints);
        Track.Id trackId = trackWithStats.first;
        TrackStatistics stats = trackWithStats.second;
        IntervalStatistics.Interval lastInterval;
        try (TrackPointIterator trackPointIterator = contentProviderUtils.getTrackPointLocationIterator(trackId, null)) {
            assertEquals(trackPointIterator.getCount(), numberOfPoints);
            IntervalStatistics intervalStatistics = new IntervalStatistics(Distance.of(1000));
            intervalStatistics.addTrackPoints(trackPointIterator);
            lastInterval = intervalStatistics.getIntervalList().get(intervalStatistics.getIntervalList().size() - 1);
        }

        SensorStatistics sensorStatistics = new SensorStatistics(HeartRate.of(180f), HeartRate.of(180f), null, null, null);

        // when
        String announcement = VoiceAnnouncementUtils.getAnnouncement(context, stats, true, true, lastInterval, sensorStatistics);

        // then
        assertEquals("total distance 14.21 kilometers in 16 minutes 39 seconds at 51.2 kilometers per hour Lap speed of 51.2 kilometers per hour Average heart rate 180 BPM Current heart rate 133 BPM", announcement);
    }

    /**
     * Builds an returns the text representing the duration's time.
     *
     * @param duration        Duration object.
     * @param showFromMinutes show minutes tough it's 0.
     * @return                text representing the duratin's time.
     */
    private String buildAndGetTimeText(Duration duration, boolean showFromMinutes) {
        long hours = Math.abs(duration.getSeconds()) / 3600;
        long minutes = (Math.abs(duration.getSeconds()) % 3600) / 60;
        long seconds = Math.abs(duration.getSeconds()) % 60;
        String hUnit = hours > 1 || hours == 0 ? "hours" : "hour";
        String mUnit = minutes > 1 || minutes == 0 ? "minutes" : "minute";
        String sUnit = seconds > 1 || seconds == 0 ? "seconds" : "second";

        String res = hours > 0 ? hours + " " + hUnit : "";
        if (hours > 0) {
            res += minutes > 0 || showFromMinutes ? " " + minutes + " " + mUnit : "";
        } else {
            res += minutes > 0 || showFromMinutes ? minutes + " " + mUnit : "";
        }
        if (hours > 0 || minutes > 0 || showFromMinutes) {
            res += " " + seconds + " " + sUnit;
        } else {
            res += seconds + " " + sUnit;
        }
        return res;
    }
}
