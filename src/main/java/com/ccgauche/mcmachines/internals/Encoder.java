package com.ccgauche.mcmachines.internals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.ccgauche.mcmachines.data.DataCompound;
import com.google.common.primitives.Ints;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * Used to encode and decode data as binary streams. Used while saving and
 * loading .dat files.
 */
public class Encoder {

	public static void encodeString(String s, OutputStream stream) throws IOException {
		encodeInt(s.length(), stream);
		stream.write(s.getBytes(StandardCharsets.UTF_8));
	}

	public static int decodeInt(InputStream stream) throws IOException {
		return Ints.fromByteArray(stream.readNBytes(4));
	}

	public static String decodeString(InputStream stream) throws IOException {
		return new String(stream.readNBytes(decodeInt(stream)), StandardCharsets.UTF_8);
	}

	public static BlockPos decodeBlockPos(InputStream stream) throws IOException {
		return new BlockPos(decodeInt(stream), decodeInt(stream), decodeInt(stream));
	}

	public static DataCompound decodeDataCompound(InputStream stream) throws IOException {
		DataCompound compound1 = new DataCompound();
		for (int i = decodeInt(stream); i > 0; i--) {
			String key = decodeString(stream);
			if (stream.read() == 1) {
				compound1.setString(key, decodeString(stream));
			} else {
				compound1.setInt(key, decodeInt(stream));
			}
		}
		return compound1;
	}

	public static void encodeBlockPos(BlockPos s, OutputStream stream) throws IOException {
		encodeInt(s.getX(), stream);
		encodeInt(s.getY(), stream);
		encodeInt(s.getZ(), stream);
	}

	public static void encodeInt(int s, OutputStream stream) throws IOException {
		stream.write(Ints.toByteArray(s));
	}

	public static void encodeWorld(Identifier s, OutputStream stream) throws IOException {
		encodeString(s.toString(), stream);
	}

	public static Identifier decodeWorld(InputStream stream) throws IOException {
		return new Identifier(decodeString(stream));
	}

	public static HashMap<Identifier, HashMap<BlockPos, DataCompound>> decodeWorldRegistry(InputStream stream)
			throws IOException {
		var map = new HashMap<Identifier, HashMap<BlockPos, DataCompound>>();
		for (int i = decodeInt(stream); i > 0; i--) {
			map.put(decodeWorld(stream), decodeRegistry(stream));
		}
		return map;
	}

	public static void encodeWorldRegistry(HashMap<Identifier, HashMap<BlockPos, DataCompound>> map,
			OutputStream stream) throws IOException {
		encodeInt(map.size(), stream);
		for (var f : map.entrySet()) {
			encodeWorld(f.getKey(), stream);
			encodeRegistry(f.getValue(), stream);
		}
	}

	public static HashMap<BlockPos, DataCompound> decodeRegistry(InputStream stream) throws IOException {
		var map = new HashMap<BlockPos, DataCompound>();
		for (int i = decodeInt(stream); i > 0; i--) {
			map.put(decodeBlockPos(stream), decodeDataCompound(stream));
		}
		return map;
	}

	public static void encodeRegistry(HashMap<BlockPos, DataCompound> compound, OutputStream stream)
			throws IOException {
		encodeInt(compound.size(), stream);
		for (var l : compound.entrySet()) {
			encodeBlockPos(l.getKey(), stream);
			encodeDataCompound(l.getValue(), stream);
		}
	}

	public static void encodeDataCompound(DataCompound compound, OutputStream stream) throws IOException {
		var map = compound.getMap();
		encodeInt(map.size(), stream);
		for (var l : map.entrySet()) {
			encodeString(l.getKey(), stream);
			if (l.getValue()instanceof String s) {
				stream.write((byte) 1);
				encodeString(s, stream);
			} else if (l.getValue()instanceof Integer s) {
				stream.write((byte) 2);
				encodeInt(s, stream);
			}
		}
	}
}
