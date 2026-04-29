package com.amr.coursemate.data

import com.amr.coursemate.data.model.*
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    fun toJson(data: BackupData): String = JSONObject().apply {
        put("version", data.version)
        put("exportedAt", data.exportedAt)

        put("classes", JSONArray().also { arr ->
            data.classes.forEach { c ->
                arr.put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("description", c.description)
                    put("notes", c.notes)
                    put("homework", c.homework)
                })
            }
        })

        put("translations", JSONArray().also { arr ->
            data.translations.forEach { t ->
                arr.put(JSONObject().apply {
                    put("id", t.id)
                    put("classId", t.classId)
                    put("bangla", t.bangla)
                    put("arabic", t.arabic)
                })
            }
        })

        put("notes", JSONArray().also { arr ->
            data.notes.forEach { n ->
                arr.put(JSONObject().apply {
                    put("id", n.id)
                    put("classId", n.classId)
                    put("content", n.content)
                    put("createdAt", n.createdAt)
                })
            }
        })

        put("dictionary", JSONArray().also { arr ->
            data.dictionary.forEach { d ->
                arr.put(JSONObject().apply {
                    put("id", d.id)
                    put("arabic", d.arabic)
                    put("bangla", d.bangla)
                })
            }
        })
    }.toString(2)

    fun fromJson(json: String): BackupData {
        val root = JSONObject(json)

        fun <T> JSONArray?.mapObjects(block: (JSONObject) -> T): List<T> =
            if (this == null) emptyList()
            else (0 until length()).map { i -> block(getJSONObject(i)) }

        val classes = root.optJSONArray("classes").mapObjects { o ->
            CourseClass(
                id = o.getLong("id"),
                name = o.getString("name"),
                description = o.optString("description", ""),
                notes = o.optString("notes", ""),
                homework = o.optString("homework", "")
            )
        }

        val translations = root.optJSONArray("translations").mapObjects { o ->
            Translation(
                id = o.getLong("id"),
                classId = o.getLong("classId"),
                bangla = o.getString("bangla"),
                arabic = o.getString("arabic")
            )
        }

        val notes = root.optJSONArray("notes").mapObjects { o ->
            Note(
                id = o.getLong("id"),
                classId = o.getLong("classId"),
                content = o.getString("content"),
                createdAt = o.getLong("createdAt")
            )
        }

        val dictionary = root.optJSONArray("dictionary").mapObjects { o ->
            Dictionary(
                id = o.getLong("id"),
                arabic = o.getString("arabic"),
                bangla = o.getString("bangla")
            )
        }

        return BackupData(
            version = root.optInt("version", 1),
            exportedAt = root.optLong("exportedAt", 0),
            classes = classes,
            translations = translations,
            notes = notes,
            dictionary = dictionary
        )
    }
}