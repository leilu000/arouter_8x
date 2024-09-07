package com.alibaba.android.arouter.demo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

///**
// * @author: created by leilu
// * email: lu.lei@hsbc.com
// *
// */
//

//
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideMyDependency(): MyDependency {
        return MyDependency("sunny")
    }
}
