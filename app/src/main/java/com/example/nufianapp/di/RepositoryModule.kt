package com.example.nufianapp.di

import com.example.nufianapp.data.repository.AuthRepository
import com.example.nufianapp.data.repository.AuthRepositoryImpl
import com.example.nufianapp.data.repository.CertificateRepository
import com.example.nufianapp.data.repository.CertificateRepositoryImpl
import com.example.nufianapp.data.repository.ConnectRepository
import com.example.nufianapp.data.repository.ConnectRepositoryImpl
import com.example.nufianapp.data.repository.ForumRepository
import com.example.nufianapp.data.repository.ForumRepositoryImpl
import com.example.nufianapp.data.repository.NewsRepositoryImpl
import com.example.nufianapp.data.repository.NotificationRepository
import com.example.nufianapp.data.repository.NotificationRepositoryImpl
import com.example.nufianapp.data.repository.ProjectRepository
import com.example.nufianapp.data.repository.ProjectRepositoryImpl
import com.example.nufianapp.data.repository.UserRepository
import com.example.nufianapp.data.repository.UserRepositoryImpl
import com.example.nufianapp.data.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindConnectRepository(connectRepositoryImpl: ConnectRepositoryImpl): ConnectRepository

    @Binds
    @Singleton
    abstract fun bindForumRepository(forumRepositoryImpl: ForumRepositoryImpl): ForumRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindCertificateRepository(certificateRepositoryImpl: CertificateRepositoryImpl): CertificateRepository


    @Binds
    @Singleton
    abstract fun bindProjectRepository(projectRepositoryImpl: ProjectRepositoryImpl): ProjectRepository


}