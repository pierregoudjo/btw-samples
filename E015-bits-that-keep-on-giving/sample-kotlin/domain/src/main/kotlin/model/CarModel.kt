package model

enum class CarModel {
    MODEL_T {
        override fun toString(): String {
            return "Model T"
        }
    },
    MODEL_V {
        override fun toString(): String {
            return "Model V"
        }
    };

    companion object {
        fun neededParts(model: CarModel) =
            when (model) {
                MODEL_T -> listOf(
                    CarPartPackage(CarPart("wheel"), 2),
                    CarPartPackage(CarPart("engine"), 1),
                    CarPartPackage(CarPart("bits and pieces"), 2)
                )
                MODEL_V -> listOf(
                    CarPartPackage(CarPart("wheel"), 2),
                    CarPartPackage(CarPart("engine"), 1),
                    CarPartPackage(CarPart("bits and pieces"), 2),
                    CarPartPackage(CarPart("chassis"), 1)
                )
            }
    }
}
