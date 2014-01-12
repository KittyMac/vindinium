package org.jousse
package bot

import controllers.routes
import play.api.libs.json._
import system.Replay

object JsonFormat {

  def apply(i: system.PlayerInput, host: String): JsObject = Json.obj(
    "game" -> apply(i.game),
    "hero" -> i.hero.map(apply(_, i.game)),
    "token" -> i.token,
    "viewUrl" -> ("http://" + host + routes.Application.visualization(i.game.id).url),
    "playUrl" -> ("http://" + host + routes.Api.move(i.game.id, i.token).url)
  )

  def apply(g: Game): JsObject = Json.obj(
    "id" -> g.id,
    "turn" -> g.turn,
    "maxTurns" -> g.maxTurns,
    "heroes" -> JsArray(g.heroes map { apply(_, g) }),
    "board" -> Json.obj(
      "size" -> g.board.size,
      "tiles" -> (g.board.posTiles map {
        case (pos, tile) => (g hero pos).fold(tile.render)(_.render)
      }).mkString
    ),
    "finished" -> g.finished
  )

  def apply(h: Hero, g: Game): JsObject = Json.obj(
    "id" -> h.id,
    "name" -> h.name,
    "pos" -> Json.obj("x" -> h.pos.x, "y" -> h.pos.y),
    "life" -> h.life,
    "gold" -> h.gold,
    "mineCount" -> g.board.countMines(h.id),
    "crashed" -> h.crashed)

  def apply(r: Replay): JsObject = Json.obj(
    "id" -> r.id,
    "games" -> JsArray(r.games)
  )
}
