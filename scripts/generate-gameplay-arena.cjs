// Deterministic GameTest fixture: a bounded 16x6x16 arena with a stone floor.
const fs = require('node:fs');
const path = require('node:path');
const zlib = require('node:zlib');
const nbt = require(process.env.DTN_NBT_MODULE || 'prismarine-nbt');
const blocks = [];
for (let x = 0; x < 16; ++x) for (let z = 0; z < 16; ++z) for (let y = 0; y < 6; ++y) {
    blocks.push({pos: {type: 'list', value: {type: 'int', value: [x, y, z]}}, state: {type: 'int', value: y === 0 ? 0 : 1}});
}
const tag = {type: 'compound', name: '', value: {
    DataVersion: {type: 'int', value: 4790},
    size: {type: 'list', value: {type: 'int', value: [16, 6, 16]}},
    palette: {type: 'list', value: {type: 'compound', value: [{Name: {type: 'string', value: 'minecraft:stone'}}, {Name: {type: 'string', value: 'minecraft:air'}}]}},
    blocks: {type: 'list', value: {type: 'compound', value: blocks}},
    entities: {type: 'list', value: {type: 'compound', value: []}}
}};
const file = path.join(__dirname, '../src/main/resources/data/doggytalents/structure/gameplay_arena.nbt');
fs.mkdirSync(path.dirname(file), {recursive: true});
fs.writeFileSync(file, zlib.gzipSync(nbt.writeUncompressed(tag)));
