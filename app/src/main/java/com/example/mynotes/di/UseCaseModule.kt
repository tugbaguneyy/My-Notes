package com.example.mynotes.di

import com.example.mynotes.data.remote.repository.FirebaseAuthImpl
import com.example.mynotes.domain.usecase.AddNoteUseCase
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.example.mynotes.domain.usecase.SignOutUseCase
import com.example.mynotes.domain.usecase.SignUpWithEmailAndPasswordUseCase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideSignInWithEmailAndPasswordUseCase(repository: FirebaseAuthImpl) : SignInWithEmailAndPasswordUseCase {
        return SignInWithEmailAndPasswordUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideSignUpWithEmailAndPasswordUseCase(repository: FirebaseAuthImpl) : SignUpWithEmailAndPasswordUseCase {
        return SignUpWithEmailAndPasswordUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideSignOutUseCase(repository: FirebaseAuthImpl) : SignOutUseCase {
        return SignOutUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideCurrentUserUseCase(repository: FirebaseAuthImpl) : CurrentUserUseCase {
        return CurrentUserUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddNoteUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : AddNoteUseCase {
        return AddNoteUseCase(db, currentUserUseCase)
    }



}