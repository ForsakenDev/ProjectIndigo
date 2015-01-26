package co.forsaken.projectindigo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import co.forsaken.projectindigo.utils.tokens.NbtServerToken;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTOutputStream;

public class NbtUtils {

  public static void writeServersToFile(ArrayList<NbtServerToken> tokens, File file) throws FileNotFoundException, IOException {
    ArrayList<CompoundTag> servers = new ArrayList<CompoundTag>();
    for (NbtServerToken t : tokens) {
      CompoundMap map = new CompoundMap();

      map.put("icon", new StringTag("icon", t.icon));
      map.put("acceptTextures", new ByteTag("acceptTextures", t.acceptTextures));
      map.put("name", new StringTag("name", t.name));
      map.put("ip", new StringTag("ip", t.ip));
      CompoundTag server = new CompoundTag("", map);
      servers.add(server);
    }
    ListTag<CompoundTag> tagServers = new ListTag<CompoundTag>("servers", CompoundTag.class, servers);
    CompoundMap m2 = new CompoundMap();
    m2.put(tagServers);
    CompoundTag finalServer = new CompoundTag("", m2);
    NBTOutputStream out = new NBTOutputStream(new FileOutputStream(file), false);
    out.writeTag(finalServer);
    out.close();
  }

}
