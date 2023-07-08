package com.example.myfoody.date


import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * Repository class that provides access to data sources.
 *
 * @property remote DataSource An instance of RemoteDataSource for accessing remote data.
 */
@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    // Expose the remote data source and local data source as a property of the Repository.
    val remote = remoteDataSource
    val local = localDataSource
}