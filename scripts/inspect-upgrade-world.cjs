// Read-only fixture inspection. Requires prismarine-nbt (or DTN_NBT_MODULE).
// Usage: node scripts/inspect-upgrade-world.cjs <closed-world-directory>
const fs = require('node:fs');
const path = require('node:path');
const zlib = require('node:zlib');
const nbt = require(process.env.DTN_NBT_MODULE || 'prismarine-nbt');

function files(root) {
    return fs.readdirSync(root, { withFileTypes: true }).flatMap(entry => {
        const full = path.join(root, entry.name);
        return entry.isDirectory() ? files(full) : [full];
    });
}

async function parse(bytes) {
    return nbt.simplify((await nbt.parse(bytes)).parsed);
}

async function inspect(root) {
    const result = { dogs: [], blocks: [], savedData: {} };
    for (const file of files(root)) {
        if (/doggytalents.*\.dat$/.test(file) || /doggytalents[/\\].*\.dat$/.test(file)) {
            result.savedData[path.relative(root, file).replaceAll('\\', '/')] = await parse(fs.readFileSync(file));
        }
        if (!file.endsWith('.mca')) continue;
        const region = fs.readFileSync(file);
        if (region.length === 0) continue; // Minecraft can create empty region placeholders.
        if (region.length < 8192) throw new Error(`Incomplete region header: ${file}`);
        for (let slot = 0; slot < 1024; ++slot) {
            const location = region.readUInt32BE(slot * 4);
            if (!location) continue;
            const offset = (location >>> 8) * 4096;
            const size = region.readUInt32BE(offset);
            const compression = region[offset + 4];
            const payload = region.subarray(offset + 5, offset + 4 + size);
            const bytes = compression === 2 ? zlib.inflateSync(payload)
                : compression === 1 ? zlib.gunzipSync(payload) : payload;
            const chunk = await parse(bytes);
            for (const entity of chunk.Entities || chunk.entities || []) {
                if (entity.id === 'doggytalents:dog') result.dogs.push(entity);
            }
            for (const block of chunk.block_entities || []) {
                if (block.id?.startsWith('doggytalents:')) result.blocks.push(block);
            }
        }
    }
    return result;
}

inspect(path.resolve(process.argv[2])).then(result => {
    process.stdout.write(JSON.stringify(result, (_, value) => typeof value === 'bigint' ? value.toString() : value, 2) + '\n');
}).catch(error => { console.error(error); process.exitCode = 1; });
