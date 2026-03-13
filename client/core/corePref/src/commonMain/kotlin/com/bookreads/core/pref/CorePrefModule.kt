package com.bookreads.core.pref

import org.koin.dsl.module

val corePrefModule =
    module {
        single<PrefService> { PrefServiceDataStore(storageProvider = get()) }
        single<LocalSessionStore> { LocalSessionStoreImpl(get()) }
    }
