package com.xborg.vendx.database

data class Machine(
    var id: String = "",
    var code: String = "Loading...",
    var mac: String = "",
    var location: Location = Location(0.0, 0.0),
    var distance: Double = 0.0
    )