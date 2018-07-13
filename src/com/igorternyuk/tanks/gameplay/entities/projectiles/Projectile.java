package com.igorternyuk.tanks.gameplay.entities.projectiles;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.Explosion;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Projectile extends Entity {

    private ProjectileType type;
    private int damage = 25;
    private boolean antiarmour = false;
    private Sprite sprite;

    public Projectile(LevelState level, ProjectileType projectileType, double x,
            double y, double speed, Direction direction) {
        super(level, EntityType.PROJECTILE, x, y, speed, direction);
        this.type = projectileType;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.PROJECTILE);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        updateSprite();
    }

    public ProjectileType getType() {
        return this.type;
    }
    
    public void setDamage(int damage){
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
    
    public void explode(){
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
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        move(frameTime);
        this.sprite.setPosition(this.x, this.y);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }

}
