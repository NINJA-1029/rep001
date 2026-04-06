package `in`.ac.agri.repository

import `in`.ac.agri.network.*

object MockData {
    val sampleParcel = Parcel(
        id = "1",
        name = "Green Valley Farm",
        centroid = listOf(-1.286389, 36.817223)
    )

    val sampleDetails = ParcelDetails(
        id = "1",
        boundary = """
            {
              "type": "Feature",
              "geometry": {
                "type": "Polygon",
                "coordinates": [[
                  [36.817, -1.286],
                  [36.818, -1.286],
                  [36.818, -1.287],
                  [36.817, -1.287],
                  [36.817, -1.286]
                ]]
              }
            }
        """.trimIndent(),
        orthomosaicUrl = "https://tiles.stadiamaps.com/tiles/alidade_satellite/{z}/{x}/{y}{r}.png", // Using a public placeholder
        ndviUrl = "https://tiles.stadiamaps.com/tiles/alidade_satellite/{z}/{x}/{y}{r}.png", // Using a public placeholder
        healthScore = HealthScore(82.0, "Healthy", 0.94),
        canopyData = CanopyData(142, 45.5, 3, 0.89),
        valuation = Valuation(450000.0, 520000.0, 580000.0, 0.91)
    )
}