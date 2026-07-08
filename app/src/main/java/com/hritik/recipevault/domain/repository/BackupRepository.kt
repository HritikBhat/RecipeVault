package com.hritik.recipevault.domain.repository

import com.hritik.recipevault.data.model.BackupData

interface BackupRepository {
    suspend fun getBackupData(): BackupData
    suspend fun restoreBackupData(backupData: BackupData)
}
