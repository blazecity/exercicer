package ch.mobpro.exercicer.data.entity.mapping

data class SummingWrapper(
    val sumSeconds: Int = 0,
    val sumMeters: Int = 0,
    val sumSets: Int = 0,
    val averageRepeats: Float = 0f,
    val averageWeight: Float = 0f
) {
    operator fun plus(addition: SummingWrapper): SummingWrapper {
        val sumSets = this.sumSets + addition.sumSets
        val sumRepeats = this.averageRepeats * this.sumSets + addition.averageRepeats * addition.sumSets
        val averageRepeats = if (sumSets == 0) 0f else sumRepeats / sumSets
        val averageWeight = if (sumRepeats == 0f) 0f else (this.averageWeight * this.averageRepeats * this.sumSets +
                addition.averageWeight * addition.averageRepeats * addition.sumSets) /
                (sumRepeats)

        return SummingWrapper(
            this.sumSeconds + addition.sumSeconds,
            this.sumMeters + addition.sumMeters,
            sumSets,
            averageRepeats,
            averageWeight
        )
    }

    operator fun plus(addition: TrainingSportTrainingTypeMapping): SummingWrapper {
        val trainingTime = (addition.training.trainingTimeHour * 60 * 60) +
                (addition.training.trainingTimeMinutes * 60) +
                addition.training.trainingTimeSeconds

        val sumSets = this.sumSets + addition.training.sets
        val sumRepeats = this.sumSets * this.averageRepeats + addition.training.sets * addition.training.repeats
        val averageRepeats = if (sumSets == 0) 0f else sumRepeats / sumSets
        val averageWeight = if (sumRepeats == 0f) 0f else (this.averageWeight * this.averageRepeats * this.sumSets +
                addition.training.weight * addition.training.repeats * addition.training.sets) /
                sumRepeats

        return SummingWrapper(
            this.sumSeconds + trainingTime,
            this.sumMeters + addition.training.trainingDistanceInMeters,
            sumSets,
            averageRepeats,
            averageWeight
        )
    }
}
