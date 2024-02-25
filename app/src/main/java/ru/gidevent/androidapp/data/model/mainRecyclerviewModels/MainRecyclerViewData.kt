package ru.gidevent.androidapp.data.model.mainRecyclerviewModels

data class MainRecyclerViewData(
    var headerDataSet: List<HeaderViewpagerItem>,
    var categoryDataSet: List<String>,
    var cardsDataSet: List<AdvertPreviewCard>
)
