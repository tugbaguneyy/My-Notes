package com.example.mynotes.di

import com.example.mynotes.data.remote.repository.FirebaseAuthImpl
import com.example.mynotes.domain.usecase.AddNoteUseCase
import com.example.mynotes.domain.usecase.ClearAllTrashUseCase
import com.example.mynotes.domain.usecase.CurrentUserUseCase
import com.example.mynotes.domain.usecase.GetAllNotesUseCase
import com.example.mynotes.domain.usecase.GetDeletedNotesUseCase
import com.example.mynotes.domain.usecase.LoadNoteUseCase
import com.example.mynotes.domain.usecase.PermanentlyDeleteNoteUseCase
import com.example.mynotes.domain.usecase.RestoreNoteUseCase
import com.example.mynotes.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.example.mynotes.domain.usecase.SignOutUseCase
import com.example.mynotes.domain.usecase.SignUpWithEmailAndPasswordUseCase
import com.example.mynotes.domain.usecase.SoftDeleteNoteUseCase
import com.example.mynotes.domain.usecase.ToggleFavoriteUseCase
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

    @Singleton
    @Provides
    fun provideGetAllNotesUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : GetAllNotesUseCase {
        return GetAllNotesUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun provideGetDeletedNotesUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : GetDeletedNotesUseCase {
        return GetDeletedNotesUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun provideLoadNoteUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : LoadNoteUseCase {
        return LoadNoteUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun provideToggleFavoriteUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun provideSoftDeleteUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : SoftDeleteNoteUseCase {
        return SoftDeleteNoteUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun provideRestoreNoteUseCase(db: FirebaseDatabase, currentUserUseCase: CurrentUserUseCase) : RestoreNoteUseCase {
        return RestoreNoteUseCase(db, currentUserUseCase)
    }

    @Singleton
    @Provides
    fun providePermanentlyDeleteNoteUseCase(db: FirebaseDatabase) : PermanentlyDeleteNoteUseCase {
        return PermanentlyDeleteNoteUseCase(db)
    }

    @Singleton
    @Provides
    fun provideClearAllTrashUseCase(db: FirebaseDatabase) : ClearAllTrashUseCase {
        return ClearAllTrashUseCase(db)
    }


}