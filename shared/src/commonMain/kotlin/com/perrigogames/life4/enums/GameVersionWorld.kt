package com.perrigogames.life4.enums

import com.perrigogames.life4.MR
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

enum class GameVersionWorld(val uiString: StringDesc) {
    UNKNOWN(MR.strings.version_unknown.desc()),
    DDR_1ST_5TH_MIX(MR.strings.version_1st_mix.desc()),
    MAX_EXTREME(MR.strings.version_max.desc()),
    SUPERNOVA(MR.strings.version_supernova.desc()),
    SUPERNOVA2(MR.strings.version_supernova.desc()),
    X(MR.strings.version_x.desc()),
    DDR_X2(MR.strings.version_x_2.desc()),
    DDR_X3_VS_2ND_MIX(MR.strings.version_x_3.desc()),
    DDR_2013(MR.strings.version_2013.desc()),
    DDR_2014(MR.strings.version_2014.desc()),
    DDR_A(MR.strings.version_a.desc()),
    DDR_A20(MR.strings.version_a20.desc()),
    DDR_A20_PLUS(MR.strings.version_a20_plus.desc()),
    DDR_A3(MR.strings.version_a3.desc()),
    DDR_WORLD(MR.strings.version_world.desc());
}

fun GameVersion.toWorldVersion(): GameVersionWorld = when (this) {
    GameVersion.UNKNOWN -> GameVersionWorld.UNKNOWN
    GameVersion.DDR_1ST_MIX,
    GameVersion.DDR_2ND_MIX,
    GameVersion.DDR_3RD_MIX,
    GameVersion.DDR_4TH_MIX,
    GameVersion.DDR_5TH_MIX -> GameVersionWorld.DDR_1ST_5TH_MIX
    GameVersion.MAX,
    GameVersion.MAX2,
    GameVersion.EXTREME -> GameVersionWorld.MAX_EXTREME
    GameVersion.SUPERNOVA -> GameVersionWorld.SUPERNOVA
    GameVersion.SUPERNOVA2 -> GameVersionWorld.SUPERNOVA2
    GameVersion.X -> GameVersionWorld.X
    GameVersion.X2 -> GameVersionWorld.DDR_X2
    GameVersion.X3_VS_2ND_MIX -> GameVersionWorld.DDR_X3_VS_2ND_MIX
    GameVersion.DDR_2013 -> GameVersionWorld.DDR_2013
    GameVersion.DDR_2014 -> GameVersionWorld.DDR_2014
    GameVersion.A -> GameVersionWorld.DDR_A
    GameVersion.A20 -> GameVersionWorld.DDR_A20
    GameVersion.A20_PLUS -> GameVersionWorld.DDR_A20_PLUS
    GameVersion.A3 -> GameVersionWorld.DDR_A3
    GameVersion.WORLD -> GameVersionWorld.DDR_WORLD
}