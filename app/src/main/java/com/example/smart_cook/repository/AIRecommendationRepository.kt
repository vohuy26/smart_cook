class AIRecommendationRepository {

    fun getSmartRecommendation(
        favorites: List<String>,
        searches: List<String>,
        viewed: List<String>
    ): List<String> {

        val result = mutableListOf<String>()

        if (favorites.contains("Burger")) {
            result.add("Cheese Burger")
            result.add("Beef Burger")
        }

        if (searches.contains("Pasta")) {
            result.add("Italian Pasta")
        }

        if (viewed.contains("Salad")) {
            result.add("Healthy Salad")
        }

        return result.distinct()
    }
}