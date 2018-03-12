package com.worldventures.wallet.service.nxt

import dagger.Module

@Module(injects = arrayOf(
      TokenizeRecordCommand::class,
      DetokenizeRecordCommand::class,
      TokenizeMultipleRecordsCommand::class,
      DetokenizeMultipleRecordsCommand::class), library = true, complete = false)
class NxtCommandsModule
