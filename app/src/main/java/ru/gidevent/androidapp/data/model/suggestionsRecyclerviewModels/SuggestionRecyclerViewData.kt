package ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels

data class SuggestionRecyclerViewData(
    val categoryList: List<SuggestionCategory>,
    val cityList: List<SuggestionCity>,
    val suggestionsList: List<SuggestionItem>,
)
