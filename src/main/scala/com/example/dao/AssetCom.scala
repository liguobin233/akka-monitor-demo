package com.example.dao

import slick.jdbc.JdbcProfile

trait Profile {


}

case class Asset(
                  assetId: String,
                  assetCode: String,
                  assetName: String,
                  baseAssetCode: String,
                ) {}

trait AssetCom {
  val profile: JdbcProfile

  import profile.api._

  class AssetTable(tag: Tag) extends Table[Asset](tag, "Asset") {

    def assetId: Rep[String] = column[String]("assetId", O.Default(""))

    def assetCode: Rep[String] = column[String]("assetCode", O.Default(""))

    def assetName: Rep[String] = column[String]("assetName", O.Default(""))

    def baseAssetCode: Rep[String] = column[String]("baseAssetCode", O.Default(""))

    override def * = (
      assetId,
      assetCode,
      assetName,
      baseAssetCode,
    ) <> ((Asset.apply _).tupled, Asset.unapply)
  }

  val AssetTable = TableQuery[AssetTable]
}
