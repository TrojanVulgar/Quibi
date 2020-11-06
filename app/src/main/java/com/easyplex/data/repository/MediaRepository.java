package com.easyplex.data.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;

import com.easyplex.data.local.dao.DownloadDao;
import com.easyplex.data.local.dao.HistoryDao;
import com.easyplex.data.local.dao.StreamListDao;
import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.local.entity.Stream;
import com.easyplex.data.model.episode.EpisodeStream;
import com.easyplex.data.model.media.Resume;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.substitles.Opensub;
import com.easyplex.data.remote.ApiInterface;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.data.local.dao.FavoriteDao;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.model.search.SearchResponse;
import com.easyplex.data.model.credits.MovieCreditsResponse;
import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.model.upcoming.Upcoming;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import hu.akarnokd.rxjava3.bridge.RxJavaBridge;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;


/**
 * Repository that acts as a mediators between different data sources; API network and ROOM database.
 * It abstracts the data sources from the rest of the app
 *
 * @author Yobex.
 */
@Singleton
public class MediaRepository {

    private final FavoriteDao favoriteDao;
    private final DownloadDao downloadDao;
    private final HistoryDao historyDao;
    private final StreamListDao streamListDao;
    ApiInterface requestMainApi;

    // Return Imdb Api from Api Interfae ( https://api.themoviedb.org/3/ )
    @Inject
    @Named("imdb")
    ApiInterface requestImdbApi;


    @Inject
    SettingsManager settingsManager;


    @Inject
    @Named("app")
    ApiInterface requestAppApi;



    @Inject
    MediaRepository(FavoriteDao favoriteDao, DownloadDao downloadDao, ApiInterface requestMainApi,
                    ApiInterface requestImdbApi, HistoryDao historyDao,StreamListDao streamListDao) {
        this.favoriteDao = favoriteDao;
        this.downloadDao = downloadDao;
        this.historyDao = historyDao;
        this.streamListDao = streamListDao;
        this.requestMainApi = requestMainApi;
        this.requestImdbApi = requestImdbApi;

    }




    // Return Serie Seasons
    public Observable<MovieResponse> getSerieSeasons(String seasonsId) {
        return requestMainApi.getSerieSeasons(seasonsId);
    }



    public Observable<MovieResponse> getAnimeSeasons(String seasonsId) {
        return requestMainApi.getAnimeSeasons(seasonsId);
    }




    // Return Random Movie
    public Observable<MovieResponse> getMoviRandom() {
        return requestMainApi.getMoviRandom(settingsManager.getSettings().getPurchaseKey());
    }





    // Return Substitle Episode
    public Observable<EpisodeStream> getEpisodeSubstitle(String tmdb) {
        return requestMainApi.getEpisodeSubstitle(tmdb);
    }





    // Return Serie Stream
    public Observable<MediaStream> getSerieStream(String tmdb) {
        return requestMainApi.getSerieStream(tmdb);
    }



    // Return Serie By Id
    public Observable<Media> getSerie(String serieTmdb) {
        return requestMainApi.getSerieById(serieTmdb, settingsManager.getSettings().getPurchaseKey());
    }


    public Observable<Media> getLiveTvById(String id) {
        return requestMainApi.getLiveById(id);
    }





    // Return Anime By Id
    public Observable<MovieResponse> getAnimes() {
        return requestMainApi.getAnimes(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Upcoming Movie By Id
    public Observable<Upcoming> getUpcomingById(int movieID) {
        return requestMainApi.getUpcomingMovieDetail(movieID);

    }



    // Return Upcoming Movies Lists
    public Observable<MovieResponse> getUpcoming() {
        return requestMainApi.getUpcomingMovies();

    }



    // Return Relateds Movies for a movie
    public Observable<MovieResponse> getRelateds(int movieID) {
        return requestMainApi.getRelatedsMovies(movieID);

    }



    // Return Casts Lists for  Movie
    public Observable<MovieCreditsResponse> getMovieCredits(int movieID) {
        return requestImdbApi.getMovieCredits(movieID,settingsManager.getSettings().getTmdbApiKey());

    }


    // Return Casts Lists for a Serie
    public Observable<MovieCreditsResponse> getSerieCredits(int movieID) {
        return requestImdbApi.getSerieCredits(movieID,settingsManager.getSettings().getTmdbApiKey());

    }



    // Return Movie By Genre
    public Observable<GenresData> getMovieByGenre(int id) {
        return requestMainApi.getGenreByID(id,settingsManager.getSettings().getPurchaseKey());
    }


    public Observable<GenresData> getMovieAllMovies() {
        return requestMainApi.getAllMovies();
    }




    // Return Serie By Genre
    public Observable<GenresData> getSerieByGenre(int id) {
        return requestMainApi.getSerieById(id,settingsManager.getSettings().getPurchaseKey());
    }


    // Return Serie By Genre
    public Observable<GenresData> getAnimeByGenre(int id) {
        return requestMainApi.getAnimeById(id,settingsManager.getSettings().getPurchaseKey());
    }


    // Return Serie By Genre
    public Observable<GenresData> getStreamingByGenre(int id) {
        return requestMainApi.getStreamById(id,settingsManager.getSettings().getPurchaseKey());
    }




    // Return Movies Genres
    public Observable<GenresByID> getMoviesGenres() {
        return requestMainApi.getGenreName(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Streamings Genres
    public Observable<GenresByID> getStreamingGenres() {
        return requestMainApi.getStreamingGenresList(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Report
    public Observable<Report> getReport(String title,String message) {
        return requestMainApi.report(title,message);
    }



    public Observable<Report> getReport2(String title,String message) {
        return requestAppApi.report2(title,message);
    }




    public Observable<Resume> getResumeMovie(int userId,String tmdb, int resumeWindow, int resumePosition,int movieDuration) {
        return requestMainApi.resumeMovie(userId,tmdb,resumeWindow,resumePosition,movieDuration);
    }



    // Return Anime Details By Id
    public Observable<Resume> getResumeById(String tmdb) {
        return requestMainApi.getResumeById(tmdb);
    }




    // Return Movie Detail by Id
    public Observable<Media> getMovie(String tmdb) {
        return requestMainApi.getMovieByTmdb(tmdb);
    }



    // Return Popular Series for HomeFragment
    public Observable<MovieResponse> getPopularSeries() {
        return requestMainApi.getSeriesPopular(settingsManager.getSettings().getPurchaseKey());
    }



    // Return ThisWeek Movies & Series for HomeFragment
    public Observable<MovieResponse> getThisWeek() {
        return requestMainApi.getThisWeekMovies(settingsManager.getSettings().getPurchaseKey());
    }




    // Return All Movies for HomeFragment
    public Observable<GenresData> getAllMovies() {
        return requestMainApi.getAllMovies();
    }


    public Observable<GenresData> getLatestAdded() {
        return requestMainApi.getLatestMovies();
    }
    public Observable<GenresData> getLatestAddedSeries() {
        return requestMainApi.getLatestSeries();
    }


    public Observable<GenresData> getLatestAddedAnimes() {
        return requestMainApi.getLatestAnimes();
    }


    public Observable<GenresData> getByYear() {
        return requestMainApi.getByYear();
    }


    public Observable<GenresData> getByYeartv() {
        return requestMainApi.getByYeartv();
    }


    public Observable<GenresData> getByYearAnimes() {
        return requestMainApi.getByYearAnimes();
    }



    public Observable<GenresData> getByRating() {
        return requestMainApi.getByRating();
    }


    public Observable<GenresData> getByRatingTv() {
        return requestMainApi.getByRatingTv();
    }


    public Observable<GenresData> getByRatingAnimes() {
        return requestMainApi.getByRatingAnimes();
    }


    public Observable<GenresData> getByViews() {
        return requestMainApi.getByViews();
    }



    public Observable<GenresData> getByViewstv() {
        return requestMainApi.getByViewstv();
    }


    public Observable<GenresData> getByViewsAnimes() {
        return requestMainApi.getByViewsAnimes();
    }



    // Return All Movies for HomeFragment
    public Observable<GenresData> getAllSeries() {
        return requestMainApi.getAllSeries();
    }


    public Observable<GenresData> getAllAnimes() {
        return requestMainApi.getAllAnimes();
    }


    // Return Popular Movies for HomeFragment
    public Observable<MovieResponse> getPopularMovies() {
        return requestMainApi.getPopularMovies(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Latest Series for HomeFragment
    public Observable<MovieResponse> getLatestSeries() {
        return requestMainApi.getSeriesRecents(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Featured Movies for HomeFragment
    public Observable<MovieResponse> getFeatured() {
        return requestMainApi.getMovieFeatured(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Recommended Series for HomeFragment
    public Observable<MovieResponse> getRecommended() {
        return requestMainApi.getRecommended(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Tranding Movies for HomeFragment
    public Observable<MovieResponse> getTrending() {
        return requestMainApi.getTrending(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Latest Movies for HomeFragment
    public Observable<MovieResponse> getLatestMovies() {
        return requestMainApi.getMovieLatest(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Suggested Movies for HomeFragment
    public Observable<MovieResponse> getSuggested() {
        return requestMainApi.getMovieSuggested(settingsManager.getSettings().getPurchaseKey());
    }



    // Handle Search
    public Observable<SearchResponse> getSearch(String query) {
        return requestMainApi.getSearch(query);
    }



    // Return Latest Streaming Channels for HomeFragment
    public Observable<MovieResponse> getLatestStreaming() {
        return requestMainApi.getLatestStreaming(settingsManager.getSettings().getPurchaseKey());
    }

    public Observable<MovieResponse> getWatchedStreaming() {
        return requestMainApi.getMostWatchedStreaming();
    }


    // Return Latest Streaming Channels for HomeFragment
    public Observable<MovieResponse> getFeaturedStreaming() {
        return requestMainApi.getFeaturedStreaming();
    }





    // Add Movie or Serie in favorite
    @SuppressLint("TimberArgCount")
    public void addResumeMovie(Download download) {
        Timber.i("Removing to database", download.getTmdbId(), download.getResumePosition());
        downloadDao.saveMediaToFavorite(download);
    }


    // Add Movie or Serie in favorite
    public void addFavorite(Media mediaDetail) {
        favoriteDao.saveMediaToFavorite(mediaDetail);
    }

    // Add Movie or Serie in favorite
    public void addStreamFavorite(Stream stream) {
        streamListDao.saveMediaToFavorite(stream);
    }


    public void addFavorite1(Download mediaDetail) {
        favoriteDao.saveMediaToFavorite1(mediaDetail);
    }


    public void addhistory(History history) {
        historyDao.saveMediaToFavorite(history);
    }



    // Remove Movie or Serie from favorite
    public void removeFavorite(Media mediaDetail) {
        Timber.i("Removing %s to database", mediaDetail.getTitle());
        favoriteDao.deleteMediaFromFavorite(mediaDetail);
    }


    // Remove Movie or Serie from favorite
    public void removeStreamFavorite(Stream stream) {
        Timber.i("Removing %s to database", stream.getTitle());
        streamListDao.deleteStream(stream);
    }


    // Remove Movie from Download
    public void removeDownload(Download download) {
        Timber.i("Removing %s to database", download.getTitle());
        downloadDao.deleteMediaFromDownload(download);
    }




    // Return Favorite Lists of Movies or Series
    public Flowable<List<Media>> getFavorites() {
        return favoriteDao.getFavoriteMovies().as(RxJavaBridge.toV3Flowable());
    }


    public Flowable<List<Stream>> getStreamFavorites() {
        return streamListDao.getFavorite().as(RxJavaBridge.toV3Flowable());
    }



    // Return Download Lists of Movies or Series
    public Flowable<List<History>> getwatchHistory() {
        return historyDao.getHistory().as(RxJavaBridge.toV3Flowable());
    }


    // Return Download Lists of Movies or Series
    public Flowable<List<Download>> getDownloads() {
        return downloadDao.getDownloadMovies().as(RxJavaBridge.toV3Flowable());
    }


    // Delete All Movies & Series from Favorite Table
    public void deleteAllFromFavorites() {
        favoriteDao.deleteMediaFromFavorite();
    }



    // Delete All History from history Table
    public void deleteAllHistory() {
        historyDao.deleteHistory();
    }




    // Return if the movie or serie is in favorite table
    public LiveData<Media> isFavorite(int movieid) {
        return favoriteDao.isFavoriteMovie(movieid);
    }


    // Return True if Movie or Serie in Featured List is in Favorite Table
    public boolean isFeaturedFavorite(int movieid) {
        return favoriteDao.isFeaturedFavoriteMovie(movieid);
    }


    public boolean isStreamFavorite(int movieid) {
        return streamListDao.isStreamFavoriteMovie(movieid);
    }

}
