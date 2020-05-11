package platgame.world;

public class Tile {
	public static Tile tiles[] = new Tile[255];
	public static byte not = 0;
	
	public static final Tile t0 = new Tile("black");
        public static final Tile t1 = new Tile("test").setSolid();
	public static final Tile t2 = new Tile("checker").setSolid();
        public static final Tile t3 = new Tile("bricks").setSolid();
        public static final Tile t4 = new Tile("greybricks").setSolid();
        public static final Tile t5 = new Tile("lightgreybricks");
        public static final Tile t6 = new Tile("negabricks").setSolid();
        public static final Tile t7 = new Tile("lightgrass");
        public static final Tile t8 = new Tile("grass").setSolid();
        public static final Tile t9 = new Tile("skyblue");
        public static final Tile t10 = new Tile("cloud");
        public static final Tile t11 = new Tile("nightcloud");
        public static final Tile t12 = new Tile("moon");
        public static final Tile t13 = new Tile("orangeblock").setSolid();
        public static final Tile t14 = new Tile("purpleblock").setSolid();
        public static final Tile t15 = new Tile("greenblock").setSolid();
        public static final Tile t16 = new Tile("wood").setSolid();
        public static final Tile t17 = new Tile("wood");
        public static final Tile t18 = new Tile("skyblue").setSolid();
        public static final Tile t19 = new Tile("Y");
        public static final Tile t20 = new Tile("O");
        public static final Tile t21 = new Tile("U");
        public static final Tile t22 = new Tile("W");
        public static final Tile t23 = new Tile("I");
        public static final Tile t24 = new Tile("N");
        
        
	
	private byte id;
	private boolean solid;
	private String texture;
	
	public Tile(String texture) {
		this.id = not;
		not++;
		this.texture = texture;
		this.solid = false;
		if (tiles[id] != null) throw new IllegalStateException("Tiles at [" + id + "] is already being used!");
		tiles[id] = this;
	}
	
	public Tile setSolid() {
		this.solid = true;
		return this;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public byte getId() {
		return id;
	}
	
	public String getTexture() {
		return texture;
	}
}
