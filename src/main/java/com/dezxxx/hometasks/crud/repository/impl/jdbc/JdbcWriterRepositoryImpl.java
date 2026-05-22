package com.dezxxx.hometasks.crud.repository.impl.jdbc;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    private final Connection connection;

    private static final String BASE_SQL = """
                SELECT
                    w.id            AS writer_id,
                    w.first_name    AS writer_first_name,
                    w.last_name     AS writer_last_name,

                    p.id            AS post_id,
                    p.title         AS post_title,
                    p.content       AS post_content,
                    p.created       AS post_created,
                    p.updated       AS post_updated,
                    p.status        AS post_status,

                    l.id            AS label_id,
                    l.name          AS label_name

                FROM writer w

                LEFT JOIN post p
                    ON w.id = p.writer_id

                LEFT JOIN post_label pl
                    ON p.id = pl.post_id

                LEFT JOIN label l
                    ON l.id = pl.label_id
            """;

    public JdbcWriterRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Writer save(Writer writer) {

        String sql = """
                INSERT INTO writer (first_name, last_name)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            ps.setString(1, writer.getFirstName());
            ps.setString(2, writer.getLastName());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    writer.setId(rs.getLong(1));
                }
            }

            return writer;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error saving writer",
                    e
            );
        }
    }

    @Override
    public List<Writer> findAll() {

        String sql = BASE_SQL + "ORDER BY w.id, p.id";

        try (PreparedStatement ps =
                connection.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()) {

            return mapWriters(rs);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error loading writers",
                    e
            );
        }
    }

    @Override
    public Optional<Writer> findById(Long id) {

        String sql = BASE_SQL + "WHERE w.id = ? ORDER BY p.id";

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                return mapWriters(rs).stream().findFirst();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error loading writer by id",
                    e
            );
        }
    }

    @Override
    public Writer update(Writer writer) {

        String sql = """
                UPDATE writer
                SET first_name = ?,
                    last_name  = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setString(1, writer.getFirstName());
            ps.setString(2, writer.getLastName());
            ps.setLong(3, writer.getId());

            ps.executeUpdate();

            return writer;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error updating writer",
                    e
            );
        }
    }

    @Override
    public void deleteById(Long id) {

        String sql = """
                DELETE FROM writer
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error deleting writer",
                    e
            );
        }
    }

    private List<Writer> mapWriters(ResultSet rs) throws SQLException {

        Map<Long, Writer> writerMap = new LinkedHashMap<>();

        while (rs.next()) {

            Long writerId = rs.getLong("writer_id");

            Writer writer = writerMap.get(writerId);

            if (writer == null) {

                writer = new Writer();
                writer.setId(writerId);
                writer.setFirstName(rs.getString("writer_first_name"));
                writer.setLastName(rs.getString("writer_last_name"));
                writer.setPosts(new ArrayList<>());

                writerMap.put(writerId, writer);
            }

            Long postId = rs.getLong("post_id");

            if (!rs.wasNull()) {

                Post post = findPost(writer, postId);

                if (post == null) {

                    post = new Post();
                    post.setId(postId);
                    post.setTitle(rs.getString("post_title"));
                    post.setContent(rs.getString("post_content"));
                    post.setStatus(rs.getString("post_status"));
                    post.setWriter(writer);
                    post.setLabels(new ArrayList<>());

                    Timestamp created = rs.getTimestamp("post_created");
                    if (created != null) {
                        post.setCreated(created.toLocalDateTime());
                    }

                    Timestamp updated = rs.getTimestamp("post_updated");
                    if (updated != null) {
                        post.setUpdated(updated.toLocalDateTime());
                    }

                    writer.getPosts().add(post);
                }

                Long labelId = rs.getLong("label_id");

                if (!rs.wasNull()) {

                    boolean alreadyExists = post.getLabels()
                            .stream()
                            .anyMatch(l -> l.getId().equals(labelId));

                    if (!alreadyExists) {

                        Label label = new Label();
                        label.setId(labelId);
                        label.setName(rs.getString("label_name"));

                        post.getLabels().add(label);
                    }
                }
            }
        }

        return new ArrayList<>(writerMap.values());
    }

    private Post findPost(Writer writer, Long postId) {

        return writer.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElse(null);
    }
}
