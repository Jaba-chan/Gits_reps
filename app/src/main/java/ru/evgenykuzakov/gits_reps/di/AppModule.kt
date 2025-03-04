package ru.evgenykuzakov.gits_reps.di

import android.content.Context
import android.util.TypedValue
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.yaml.snakeyaml.Yaml
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.evgenykuzakov.gits_reps.data.remote.GitHubApi
import ru.evgenykuzakov.gits_reps.data.repository.AppRepositoryImpl
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.data.storage.ProgrammingLanguages
import ru.evgenykuzakov.gits_reps.domain.repository.AppRepository
import javax.inject.Singleton

private const val BASE_URL = "https://api.github.com/"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideGitHubApi(): GitHubApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepository(api: GitHubApi, storage: KeyValueStorage): AppRepository {
        return AppRepositoryImpl(api, storage)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideYaml(): Yaml {
        return Yaml()
    }

    @Provides
    @Singleton
    fun provideProgrammingLanguages(
        yaml: Yaml,
        @ApplicationContext context: Context
    ): ProgrammingLanguages {
        return ProgrammingLanguages(yaml, context)
    }

}
