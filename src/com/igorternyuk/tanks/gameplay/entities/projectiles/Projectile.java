package com.igorternyuk.tanks.gameplay.entities.projectiles;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.tilemap.BrickTile;
import com.igorternyuk.tanks.gameplay.tilemap.MetalTile;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gameplay.tilemap.TileType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author igor
 */
public class Projectile extends Entity {

    private ProjectileType type;
    private int damage = 25;
    private boolean antiarmour = false;
    private Sprite sprite;
    private boolean bushCrearing = false;
    private int ownerId = 0;

    public Projectile(LevelState level, ProjectileType projectileType, double x,
            double y, double speed, Direction direction) {
        super(level, EntityType.PROJECTILE, x, y, speed, direction);
        this.type = projectileType;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.PROJECTILE);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        updateSprite();
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isBushCrearing() {
        return this.bushCrearing;
    }

    public void setBushCrearing(boolean bushCrearing) {
        this.bushCrearing = bushCrearing;
    }

    public ProjectileType getType() {
        return this.type;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    public boolean isAntiarmour() {
        return this.antiarmour;
    }

    public void setAntiarmour(boolean antiarmour) {
        this.antiarmour = antiarmour;
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !isOutOfBounds();
    }
    
    public void explode() {
        super.explode(ExplosionType.SMALL);
        destroy();
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateSprite();
    }

    private void updateSprite() {
        this.sprite.setSourceRect(ProjectileType.getSourceRect(this.direction));
        this.sprite.setPosition(this.x, this.y);
    }

    @Override
    public int getWidth() {
        return this.sprite.getWidth();
    }

    @Override
    public int getHeight() {
        return this.sprite.getHeight();
    }

    @Override
    public Rectangle getBoundingRect() {
        Rectangle spriteBoundingRect = super.getBoundingRect();
        Rectangle collisionRect = new Rectangle(spriteBoundingRect.x,
                spriteBoundingRect.y, spriteBoundingRect.width,
                spriteBoundingRect.height);
        int effectiveCollisionSize = 1 + Game.HALF_TILE_SIZE + 1;
        if (this.direction.isVertical()) {
            int dx = (effectiveCollisionSize - spriteBoundingRect.width) / 2;
            collisionRect.x = spriteBoundingRect.x - dx;
            collisionRect.width = effectiveCollisionSize;
        } else if (this.direction.isHorizontal()) {
            int dy = (effectiveCollisionSize - spriteBoundingRect.height) / 2;
            collisionRect.y = spriteBoundingRect.y - dy;
            collisionRect.height = effectiveCollisionSize;
        }
        return collisionRect;
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        move(frameTime);
        handleMapCollision();
        this.sprite.setPosition(getX(), getY());
    }

    private void handleMapCollision() {
        TileMap tileMap = this.level.getTileMap();
        List<Tile> tiles = tileMap.getIntersectedTiles(this);
        boolean collisionOccured = false;
        for (int i = 0; i < tiles.size(); ++i) {
            Tile collidedTile = tiles.get(i);
            if (collidedTile.getType() == TileType.BRICK) {
                BrickTile brickTile = (BrickTile) collidedTile;
                if (brickTile.checkIfCollision(this)) {
                    brickTile.handleProjectileCollision(this);
                    collisionOccured = true;
                }
            } else if (collidedTile.getType() == TileType.METAL) {
                MetalTile metalTile = (MetalTile) collidedTile;
                if (metalTile.checkIfCollision(this)) {
                    metalTile.hit(this);
                    collisionOccured = true;
                }
            } else if (collidedTile.getType() == TileType.BUSH) {
                if (collidedTile.checkIfCollision(this)) {
                    if (this.bushCrearing) {
                        tileMap.destroyTile(collidedTile.getRow(), collidedTile.
                                getColumn());
                        collisionOccured = true;
                    }
                }
            }
        }

        if (collisionOccured) {
            this.explode();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }

}
