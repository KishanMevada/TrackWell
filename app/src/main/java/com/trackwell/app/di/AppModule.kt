package com.trackwell.app.di

import com.trackwell.app.data.medicine.MedicineRepository
import com.trackwell.app.data.medicine.MedicineRepositoryImpl
import com.trackwell.app.data.repository.DailyHealthRepositoryImpl
import com.trackwell.app.data.repository.FluidRepositoryImpl
import com.trackwell.app.data.repository.SugarRepositoryImpl
import com.trackwell.app.domain.repository.DailyHealthRepository
import com.trackwell.app.domain.repository.FluidRepository
import com.trackwell.app.domain.repository.SugarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindFluidRepository(impl: FluidRepositoryImpl): FluidRepository

    @Binds
    @Singleton
    abstract fun bindSugarRepository(impl: SugarRepositoryImpl): SugarRepository

    @Binds
    @Singleton
    abstract fun bindDailyHealthRepository(impl: DailyHealthRepositoryImpl): DailyHealthRepository

    @Binds
    @Singleton
    abstract fun bindMedicineRepository(impl: MedicineRepositoryImpl): MedicineRepository
}
